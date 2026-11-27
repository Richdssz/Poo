
package com.unicap.pi3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CadastroContaDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 160, message = "O nome deve ter no máximo 160 caracteres")
    private String nome;

    @NotBlank(message = "O CRP é obrigatório")
    @Pattern(
        regexp = "^[0-9]{2}/[0-9]{4,6}$",
        message = "O CRP deve seguir o formato 00/000000"
    )
    private String crp;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    private String senha;

    public CadastroContaDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCrp() {
        return crp;
    }

    public void setCrp(String crp) {
        this.crp = crp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
