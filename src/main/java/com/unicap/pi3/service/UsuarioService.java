
package com.unicap.pi3.service;

import com.unicap.pi3.dto.AtualizarUsuarioDTO;
import com.unicap.pi3.dto.CadastroContaDTO;
import com.unicap.pi3.dto.UsuarioFiltroDTO;
import com.unicap.pi3.dto.UsuarioMeResponseDTO;
import com.unicap.pi3.dto.UsuarioResponseDTO;
import com.unicap.pi3.entity.Psicologo;
import com.unicap.pi3.entity.Usuario;
import com.unicap.pi3.exception.ConflitoException;
import com.unicap.pi3.exception.RecursoNaoEncontradoException;
import com.unicap.pi3.integration.SupabaseAuthService;
import com.unicap.pi3.repository.PsicologoRepository;
import com.unicap.pi3.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final Logger logger =
            LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PsicologoRepository psicologoRepository;
    private final SupabaseAuthService supabaseAuthService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PsicologoRepository psicologoRepository,
            SupabaseAuthService supabaseAuthService) {

        this.usuarioRepository = usuarioRepository;
        this.psicologoRepository = psicologoRepository;
        this.supabaseAuthService = supabaseAuthService;
    }

    // CADASTRAR USUÁRIO

    @Transactional
    public UsuarioResponseDTO cadastrar(CadastroContaDTO dados) {

        String email = dados.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String crp = dados.getCrp().trim();
        String nome = dados.getNome().trim();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException(
                    "Este e-mail já está cadastrado."
            );
        }

        if (psicologoRepository.existsByCrp(crp)) {
            throw new ConflitoException(
                    "Este CRP já está cadastrado."
            );
        }

        UUID authId = supabaseAuthService.criarUsuario(
                email,
                dados.getSenha()
        );

        registrarCompensacaoNoRollback(
                () -> supabaseAuthService.excluirUsuario(authId),
                "Não foi possível excluir o usuário do Supabase Auth após falha no cadastro."
        );

        Psicologo psicologo = new Psicologo();
        psicologo.setNome(nome);
        psicologo.setCrp(crp);

        Psicologo psicologoSalvo =
                psicologoRepository.save(psicologo);

        Usuario usuario = new Usuario();
        usuario.setId(authId);
        usuario.setPsicologoId(psicologoSalvo.getId());
        usuario.setEmail(email);
        usuario.setPapel("PSICOLOGO");
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // CONSULTAR PRÓPRIO PERFIL

    public UsuarioMeResponseDTO buscarUsuarioAutenticado(UUID id) {

        Usuario usuario = buscarUsuarioExistente(id);
        verificarContaAtiva(usuario);

        UsuarioMeResponseDTO resposta =
                new UsuarioMeResponseDTO();

        resposta.setId(usuario.getId());
        resposta.setPsicologoId(usuario.getPsicologoId());
        resposta.setEmail(usuario.getEmail());
        resposta.setPapel(usuario.getPapel());
        resposta.setAtivo(usuario.getAtivo());

        if (usuario.getPsicologoId() != null) {

            Psicologo psicologo = psicologoRepository
                    .findById(usuario.getPsicologoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Perfil do psicólogo não encontrado."
                    ));

            resposta.setNome(psicologo.getNome());
            resposta.setCrp(psicologo.getCrp());
        }

        return resposta;
    }

    // LISTAR USUÁRIOS

    public Page<UsuarioResponseDTO> listarUsuarios(
            UUID solicitanteId,
            UsuarioFiltroDTO filtros,
            int pagina,
            int tamanho) {

        Usuario solicitante =
                buscarUsuarioExistente(solicitanteId);

        verificarAdministrador(solicitante);

        if (pagina < 0 || tamanho < 1 || tamanho > 100) {
            throw new IllegalArgumentException(
                    "Parâmetros de paginação inválidos."
            );
        }

        Pageable pageable = PageRequest.of(pagina, tamanho);

        String papel = filtros != null
                ? filtros.getPapel()
                : null;

        Boolean ativo = filtros != null
                ? filtros.getAtivo()
                : null;

        Page<Usuario> usuarios;

        if (papel != null && !papel.isBlank()) {

            papel = papel.trim().toUpperCase(Locale.ROOT);

            if (!papel.equals("ADMIN")
                    && !papel.equals("PSICOLOGO")) {

                throw new IllegalArgumentException(
                        "Papel inválido."
                );
            }

            usuarios = ativo == null
                    ? usuarioRepository.findByPapel(papel, pageable)
                    : usuarioRepository.findByPapelAndAtivo(
                            papel,
                            ativo,
                            pageable
                    );

        } else if (ativo != null) {

            usuarios = usuarioRepository.findByAtivo(
                    ativo,
                    pageable
            );

        } else {

            usuarios = usuarioRepository.findAll(pageable);
        }

        return usuarios.map(this::converterParaResponse);
    }

    // CONSULTAR USUÁRIO POR ID

    public UsuarioResponseDTO buscarPorId(
            UUID solicitanteId,
            UUID usuarioId) {

        Usuario solicitante =
                buscarUsuarioExistente(solicitanteId);

        verificarContaAtiva(solicitante);

        Usuario usuario =
                buscarUsuarioExistente(usuarioId);

        boolean administrador =
                "ADMIN".equals(solicitante.getPapel());

        boolean proprioUsuario =
                solicitante.getId().equals(usuario.getId());

        if (!administrador && !proprioUsuario) {
            throw new AccessDeniedException(
                    "Você não tem permissão para consultar este usuário."
            );
        }

        return converterParaResponse(usuario);
    }

    // ATUALIZAR USUÁRIO

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(
            UUID solicitanteId,
            UUID usuarioId,
            AtualizarUsuarioDTO dados) {

        Usuario solicitante =
                buscarUsuarioExistente(solicitanteId);

        verificarContaAtiva(solicitante);

        Usuario usuario =
                buscarUsuarioExistente(usuarioId);

        boolean administrador =
                "ADMIN".equals(solicitante.getPapel());

        boolean proprioUsuario =
                solicitante.getId().equals(usuario.getId());

        if (!administrador && !proprioUsuario) {
            throw new AccessDeniedException(
                    "Você não tem permissão para atualizar este usuário."
            );
        }

        if (!administrador
                && (dados.getAtivo() != null
                || dados.getPapel() != null)) {

            throw new AccessDeniedException(
                    "Somente administradores podem alterar status ou papel."
            );
        }

        // Impede que o administrador perca o próprio acesso administrativo.

        if (administrador && proprioUsuario) {

            if (Boolean.FALSE.equals(dados.getAtivo())) {
                throw new AccessDeniedException(
                        "Você não pode desativar sua própria conta."
                );
            }

            if (dados.getPapel() != null
                    && !"ADMIN".equals(dados.getPapel())) {

                throw new AccessDeniedException(
                        "Você não pode remover seu próprio papel de administrador."
                );
            }
        }

        // ATUALIZAÇÃO DE E-MAIL

        if (dados.getEmail() != null
                && !dados.getEmail().isBlank()) {

            String novoEmail = dados.getEmail()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            usuarioRepository.findByEmail(novoEmail)
                    .ifPresent(outroUsuario -> {

                        if (!outroUsuario.getId()
                                .equals(usuario.getId())) {

                            throw new ConflitoException(
                                    "Este e-mail já está cadastrado."
                            );
                        }
                    });

            String emailAnterior = usuario.getEmail();

            if (!novoEmail.equals(emailAnterior)) {

                supabaseAuthService.atualizarEmail(
                        usuario.getId(),
                        novoEmail
                );

                registrarCompensacaoNoRollback(
                        () -> supabaseAuthService.atualizarEmail(
                                usuario.getId(),
                                emailAnterior
                        ),
                        "Não foi possível restaurar o e-mail anterior no Supabase Auth."
                );

                usuario.setEmail(novoEmail);
            }
        }

        // ATUALIZAÇÃO DE STATUS E PAPEL

        if (administrador) {

            if (dados.getAtivo() != null) {
                usuario.setAtivo(dados.getAtivo());
            }

            if (dados.getPapel() != null) {

                String novoPapel = dados.getPapel()
                        .trim()
                        .toUpperCase(Locale.ROOT);

                if (!novoPapel.equals("ADMIN")
                        && !novoPapel.equals("PSICOLOGO")) {

                    throw new IllegalArgumentException(
                            "Papel inválido."
                    );
                }

                if ("ADMIN".equals(novoPapel)) {

                    usuario.setPapel("ADMIN");
                    usuario.setPsicologoId(null);

                } else {

                    if (usuario.getPsicologoId() == null) {
                        throw new IllegalArgumentException(
                                "Não é possível definir o papel PSICOLOGO sem um perfil de psicólogo vinculado."
                        );
                    }

                    usuario.setPapel("PSICOLOGO");
                }
            }
        }

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // DESATIVAR USUÁRIO (EXCLUSÃO LÓGICA)

    @Transactional
    public void desativarUsuario(
            UUID solicitanteId,
            UUID usuarioId) {

        Usuario solicitante =
                buscarUsuarioExistente(solicitanteId);

        verificarAdministrador(solicitante);

        if (solicitanteId.equals(usuarioId)) {
            throw new AccessDeniedException(
                    "Você não pode desativar sua própria conta."
            );
        }

        Usuario usuario =
                buscarUsuarioExistente(usuarioId);

        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }

    // REATIVAR USUÁRIO

    @Transactional
    public UsuarioResponseDTO reativarUsuario(
            UUID solicitanteId,
            UUID usuarioId) {

        Usuario solicitante =
                buscarUsuarioExistente(solicitanteId);

        verificarAdministrador(solicitante);

        Usuario usuario =
                buscarUsuarioExistente(usuarioId);

        usuario.setAtivo(true);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    // MÉTODOS AUXILIARES

    private Usuario buscarUsuarioExistente(UUID id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado."
                ));
    }

    private void verificarContaAtiva(Usuario usuario) {

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AccessDeniedException(
                    "Esta conta está inativa."
            );
        }
    }

    private void verificarAdministrador(Usuario usuario) {

        verificarContaAtiva(usuario);

        if (!"ADMIN".equals(usuario.getPapel())) {
            throw new AccessDeniedException(
                    "Somente administradores podem realizar esta operação."
            );
        }
    }

    private UsuarioResponseDTO converterParaResponse(
            Usuario usuario) {

        UsuarioResponseDTO resposta =
                new UsuarioResponseDTO();

        resposta.setId(usuario.getId());
        resposta.setPsicologoId(usuario.getPsicologoId());
        resposta.setEmail(usuario.getEmail());
        resposta.setPapel(usuario.getPapel());
        resposta.setAtivo(usuario.getAtivo());
        resposta.setCriadoEm(usuario.getCriadoEm());

        if (usuario.getPsicologoId() != null) {

            psicologoRepository.findById(usuario.getPsicologoId())
                    .ifPresent(psicologo -> {
                        resposta.setNome(psicologo.getNome());
                        resposta.setCrp(psicologo.getCrp());
                    });
        }

        return resposta;
    }

    // COMPENSAÇÃO DE OPERAÇÕES EXTERNAS

    private void registrarCompensacaoNoRollback(
            Runnable compensacao,
            String mensagemErro) {

        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            throw new IllegalStateException(
                    "Não foi possível registrar a compensação da transação."
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {

                        if (status == STATUS_ROLLED_BACK) {

                            try {
                                compensacao.run();

                            } catch (RuntimeException erro) {

                                logger.error(
                                        mensagemErro,
                                        erro
                                );
                            }
                        }
                    }
                }
        );
    }
}
