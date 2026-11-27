
package com.unicap.pi3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AtualizarUsuarioDTO {

    @Email(message = "Informe um e-mail válido")
    @Size(
        max = 254,
        message = "O e-mail deve ter no máximo 254 caracteres"
    )
    private String email;

    private Boolean ativo;

    @Pattern(
        regexp = "(?i)^\\s*(ADMIN|PSICOLOGO)\\s*$",
        message = "O papel deve ser ADMIN ou PSICOLOGO"
    )
    private String papel;

    public AtualizarUsuarioDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }
}
