
package com.unicap.pi3.security;

import com.unicap.pi3.entity.Usuario;
import com.unicap.pi3.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioAtivoFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAtivoFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String caminho = request.getServletPath();

        return caminho.equals("/api/health")
                || caminho.equals("/api/usuarios/cadastro")
                || caminho.startsWith("/swagger-ui/")
                || caminho.equals("/swagger-ui.html")
                || caminho.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth
                && authentication.isAuthenticated()) {

            try {
                UUID usuarioId = UUID.fromString(
                        jwtAuth.getToken().getSubject()
                );

                Optional<Usuario> usuario =
                        usuarioRepository.findById(usuarioId);

                if (usuario.isEmpty()
                        || !Boolean.TRUE.equals(usuario.get().getAtivo())) {

                    SecurityContextHolder.clearContext();

                    responderErro(
                            response,
                            HttpStatus.FORBIDDEN,
                            "Conta inativa ou usuário não encontrado."
                    );
                    return;
                }

            } catch (IllegalArgumentException erro) {

                SecurityContextHolder.clearContext();

                responderErro(
                        response,
                        HttpStatus.UNAUTHORIZED,
                        "Identidade do usuário inválida."
                );
                return;

            } catch (DataAccessException erro) {

                SecurityContextHolder.clearContext();

                responderErro(
                        response,
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Não foi possível verificar o status da conta."
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void responderErro(
            HttpServletResponse response,
            HttpStatus status,
            String mensagem) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"erro\":\"" + mensagem + "\"}"
        );
    }
}
