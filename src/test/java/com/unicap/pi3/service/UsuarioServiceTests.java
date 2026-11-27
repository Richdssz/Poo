
package com.unicap.pi3.service;

import com.unicap.pi3.dto.AtualizarUsuarioDTO;
import com.unicap.pi3.dto.UsuarioResponseDTO;
import com.unicap.pi3.entity.Psicologo;
import com.unicap.pi3.entity.Usuario;
import com.unicap.pi3.integration.SupabaseAuthService;
import com.unicap.pi3.repository.PsicologoRepository;
import com.unicap.pi3.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private SupabaseAuthService supabaseAuthService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveConsultarPerfilDoProprioUsuario() {

        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setPsicologoId(1L);
        usuario.setEmail("psicologo@teste.com");
        usuario.setPapel("PSICOLOGO");
        usuario.setAtivo(true);

        Psicologo psicologo = new Psicologo();
        psicologo.setId(1L);
        psicologo.setNome("Psicólogo Teste");
        psicologo.setCrp("02/12345");

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(psicologoRepository.findById(1L))
                .thenReturn(Optional.of(psicologo));

        var resposta =
                usuarioService.buscarUsuarioAutenticado(id);

        assertEquals(id, resposta.getId());
        assertEquals("psicologo@teste.com", resposta.getEmail());
        assertEquals("PSICOLOGO", resposta.getPapel());
        assertEquals("Psicólogo Teste", resposta.getNome());
        assertEquals("02/12345", resposta.getCrp());
        assertTrue(resposta.getAtivo());
    }

    @Test
    void naoDeveConsultarPerfilDeUsuarioInativo() {

        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail("inativo@teste.com");
        usuario.setPapel("PSICOLOGO");
        usuario.setAtivo(false);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                AccessDeniedException.class,
                () -> usuarioService.buscarUsuarioAutenticado(id)
        );

        verify(psicologoRepository, never()).findById(anyLong());
    }

    @Test
    void psicologoNaoDeveConsultarOutroUsuario() {

        UUID idSolicitante = UUID.randomUUID();
        UUID idOutroUsuario = UUID.randomUUID();

        Usuario solicitante = new Usuario();
        solicitante.setId(idSolicitante);
        solicitante.setEmail("psicologo@teste.com");
        solicitante.setPapel("PSICOLOGO");
        solicitante.setAtivo(true);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(idOutroUsuario);
        outroUsuario.setEmail("outro@teste.com");
        outroUsuario.setPapel("PSICOLOGO");
        outroUsuario.setAtivo(true);

        when(usuarioRepository.findById(idSolicitante))
                .thenReturn(Optional.of(solicitante));

        when(usuarioRepository.findById(idOutroUsuario))
                .thenReturn(Optional.of(outroUsuario));

        assertThrows(
                AccessDeniedException.class,
                () -> usuarioService.buscarPorId(
                        idSolicitante,
                        idOutroUsuario
                )
        );

        verify(psicologoRepository, never()).findById(anyLong());
    }
    @Test
void administradorDeveConsultarOutroUsuario() {

    UUID idAdmin = UUID.randomUUID();
    UUID idUsuario = UUID.randomUUID();

    Usuario admin = new Usuario();
    admin.setId(idAdmin);
    admin.setEmail("admin@teste.com");
    admin.setPapel("ADMIN");
    admin.setAtivo(true);

    Usuario usuario = new Usuario();
    usuario.setId(idUsuario);
    usuario.setPsicologoId(1L);
    usuario.setEmail("psicologo@teste.com");
    usuario.setPapel("PSICOLOGO");
    usuario.setAtivo(true);

    Psicologo psicologo = new Psicologo();
    psicologo.setId(1L);
    psicologo.setNome("Psicólogo Teste");
    psicologo.setCrp("02/12345");

    when(usuarioRepository.findById(idAdmin))
            .thenReturn(Optional.of(admin));

    when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.of(usuario));

    when(psicologoRepository.findById(1L))
            .thenReturn(Optional.of(psicologo));

    var resposta = usuarioService.buscarPorId(idAdmin, idUsuario);

    assertEquals(idUsuario, resposta.getId());
    assertEquals("psicologo@teste.com", resposta.getEmail());
    assertEquals("PSICOLOGO", resposta.getPapel());
    assertEquals("Psicólogo Teste", resposta.getNome());
}
@Test
void psicologoNaoDeveListarUsuarios() {

    UUID idPsicologo = UUID.randomUUID();

    Usuario psicologo = new Usuario();
    psicologo.setId(idPsicologo);
    psicologo.setEmail("psicologo@teste.com");
    psicologo.setPapel("PSICOLOGO");
    psicologo.setAtivo(true);

    when(usuarioRepository.findById(idPsicologo))
            .thenReturn(Optional.of(psicologo));

    assertThrows(
            AccessDeniedException.class,
            () -> usuarioService.listarUsuarios(
                    idPsicologo,
                    new com.unicap.pi3.dto.UsuarioFiltroDTO(),
                    0,
                    10
            )
    );

    verify(usuarioRepository, never()).findAll(any(
            org.springframework.data.domain.Pageable.class
    ));
}
@Test
void administradorDeveDesativarOutroUsuario() {
    UUID administradorId = UUID.randomUUID();
    UUID usuarioId = UUID.randomUUID();

    Usuario administrador = new Usuario();
    administrador.setId(administradorId);
    administrador.setPapel("ADMIN");
    administrador.setAtivo(true);

    Usuario usuario = new Usuario();
    usuario.setId(usuarioId);
    usuario.setPapel("PSICOLOGO");
    usuario.setAtivo(true);

    when(usuarioRepository.findById(administradorId))
            .thenReturn(Optional.of(administrador));

    when(usuarioRepository.findById(usuarioId))
            .thenReturn(Optional.of(usuario));

    when(usuarioRepository.save(usuario))
            .thenReturn(usuario);

    usuarioService.desativarUsuario(administradorId, usuarioId);

    assertFalse(usuario.getAtivo());
    verify(usuarioRepository).save(usuario);
}
@Test
void administradorDeveReativarOutroUsuario() {
    UUID administradorId = UUID.randomUUID();
    UUID usuarioId = UUID.randomUUID();

    Usuario administrador = new Usuario();
    administrador.setId(administradorId);
    administrador.setPapel("ADMIN");
    administrador.setAtivo(true);

    Usuario usuario = new Usuario();
    usuario.setId(usuarioId);
    usuario.setPapel("PSICOLOGO");
    usuario.setAtivo(false);
    usuario.setEmail("psicologo@email.com");

    when(usuarioRepository.findById(administradorId))
            .thenReturn(Optional.of(administrador));

    when(usuarioRepository.findById(usuarioId))
            .thenReturn(Optional.of(usuario));

    when(usuarioRepository.save(usuario))
            .thenReturn(usuario);

    UsuarioResponseDTO resposta =
            usuarioService.reativarUsuario(administradorId, usuarioId);

    assertTrue(usuario.getAtivo());
    assertEquals(usuarioId, resposta.getId());
    assertEquals("psicologo@email.com", resposta.getEmail());

    verify(usuarioRepository).save(usuario);
}
@Test
void administradorNaoDeveDesativarPropriaConta() {
    UUID administradorId = UUID.randomUUID();

    Usuario administrador = new Usuario();
    administrador.setId(administradorId);
    administrador.setPapel("ADMIN");
    administrador.setAtivo(true);

    when(usuarioRepository.findById(administradorId))
            .thenReturn(Optional.of(administrador));

    assertThrows(
            AccessDeniedException.class,
            () -> usuarioService.desativarUsuario(
                    administradorId,
                    administradorId
            )
    );

    verify(usuarioRepository, never()).save(any(Usuario.class));
}
@Test
void psicologoNaoDeveReativarUsuario() {
    UUID psicologoId = UUID.randomUUID();
    UUID usuarioId = UUID.randomUUID();

    Usuario psicologo = new Usuario();
    psicologo.setId(psicologoId);
    psicologo.setPapel("PSICOLOGO");
    psicologo.setAtivo(true);

    when(usuarioRepository.findById(psicologoId))
            .thenReturn(Optional.of(psicologo));

    assertThrows(
            AccessDeniedException.class,
            () -> usuarioService.reativarUsuario(
                    psicologoId,
                    usuarioId
            )
    );

    verify(usuarioRepository, never()).save(any(Usuario.class));
}
@Test
void psicologoDeveAtualizarProprioEmail() {
    UUID psicologoId = UUID.randomUUID();

    Usuario psicologo = new Usuario();
    psicologo.setId(psicologoId);
    psicologo.setPapel("PSICOLOGO");
    psicologo.setAtivo(true);
    psicologo.setEmail("antigo@email.com");
    psicologo.setPsicologoId(10L);

    AtualizarUsuarioDTO dados = new AtualizarUsuarioDTO();
    dados.setEmail("  NOVO@EMAIL.COM  ");

    when(usuarioRepository.findById(psicologoId))
            .thenReturn(Optional.of(psicologo));

    when(usuarioRepository.findByEmail("novo@email.com"))
            .thenReturn(Optional.empty());

    when(usuarioRepository.save(psicologo))
            .thenReturn(psicologo);

    UsuarioResponseDTO resposta =
            usuarioService.atualizarUsuario(
                    psicologoId,
                    psicologoId,
                    dados
            );

    assertEquals("novo@email.com", psicologo.getEmail());
    assertEquals("novo@email.com", resposta.getEmail());

    verify(supabaseAuthService).atualizarEmail(
            psicologoId,
            "novo@email.com"
    );

    verify(usuarioRepository).save(psicologo);
}
}
