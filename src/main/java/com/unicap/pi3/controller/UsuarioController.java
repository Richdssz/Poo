
package com.unicap.pi3.controller;

import com.unicap.pi3.dto.AtualizarUsuarioDTO;
import com.unicap.pi3.dto.CadastroContaDTO;
import com.unicap.pi3.dto.UsuarioFiltroDTO;
import com.unicap.pi3.dto.UsuarioMeResponseDTO;
import com.unicap.pi3.dto.UsuarioResponseDTO;
import com.unicap.pi3.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // CADASTRAR USUÁRIO

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody CadastroContaDTO dados) {

        UsuarioResponseDTO resposta = usuarioService.cadastrar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    // CONSULTAR PRÓPRIO PERFIL

    @GetMapping("/me")
    public ResponseEntity<UsuarioMeResponseDTO> buscarMeuUsuario(
            Authentication authentication) {

        UUID id = UUID.fromString(authentication.getName());

        UsuarioMeResponseDTO resposta =
                usuarioService.buscarUsuarioAutenticado(id);

        return ResponseEntity.ok(resposta);
    }

    // LISTAR USUÁRIOS

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(
            Authentication authentication,
            @ModelAttribute UsuarioFiltroDTO filtros,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        UUID id = UUID.fromString(authentication.getName());

        Page<UsuarioResponseDTO> resposta =
                usuarioService.listarUsuarios(id, filtros, pagina, tamanho);

        return ResponseEntity.ok(resposta);
    }

    // CONSULTAR USUÁRIO POR ID

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID solicitanteId = UUID.fromString(authentication.getName());

        UsuarioResponseDTO resposta =
                usuarioService.buscarPorId(solicitanteId, id);

        return ResponseEntity.ok(resposta);
    }

    // ATUALIZAR USUÁRIO

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarUsuarioDTO dados,
            Authentication authentication) {

        UUID solicitanteId = UUID.fromString(authentication.getName());

        UsuarioResponseDTO resposta =
                usuarioService.atualizarUsuario(
                        solicitanteId, id, dados
                );

        return ResponseEntity.ok(resposta);
    }

    // DESATIVAR USUÁRIO

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarUsuario(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID solicitanteId = UUID.fromString(authentication.getName());

        usuarioService.desativarUsuario(solicitanteId, id);

        return ResponseEntity.noContent().build();
    }

    // REATIVAR USUÁRIO

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<UsuarioResponseDTO> reativarUsuario(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID solicitanteId = UUID.fromString(authentication.getName());

        UsuarioResponseDTO resposta =
                usuarioService.reativarUsuario(solicitanteId, id);

        return ResponseEntity.ok(resposta);
    }
}
