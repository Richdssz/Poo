
package com.unicap.pi3.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseAuthService {

    private final RestClient restClient;

    public SupabaseAuthService(
            @Value("${SUPABASE_URL}") String supabaseUrl,
            @Value("${SUPABASE_SERVICE_ROLE_KEY}") String serviceRoleKey) {

        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/auth/v1")
                .defaultHeader("apikey", serviceRoleKey)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + serviceRoleKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    // CRIAR USUÁRIO NO SUPABASE AUTH
    public UUID criarUsuario(String email, String senha) {

        Map<String, Object> dados = Map.of(
                "email", email,
                "password", senha
        );

        Map<?, ?> resposta = restClient.post()
                .uri("/admin/users")
                .body(dados)
                .retrieve()
                .body(Map.class);

        if (resposta == null || resposta.get("id") == null) {
            throw new IllegalStateException(
                    "O Supabase não retornou o ID do usuário criado."
            );
        }

        return UUID.fromString(resposta.get("id").toString());
    }

    // ATUALIZAR E-MAIL NO SUPABASE AUTH
    public void atualizarEmail(UUID id, String novoEmail) {

        Map<String, Object> dados = Map.of(
                "email", novoEmail
        );

        restClient.put()
                .uri("/admin/users/{id}", id)
                .body(dados)
                .retrieve()
                .toBodilessEntity();
    }

    // EXCLUIR USUÁRIO DO SUPABASE AUTH
    public void excluirUsuario(UUID id) {

        restClient.delete()
                .uri("/admin/users/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
