
package com.unicap.pi3.dto;

import java.util.UUID;

public class UsuarioMeResponseDTO {

    private UUID id;
    private Long psicologoId;
    private String nome;
    private String crp;
    private String email;
    private String papel;
    private Boolean ativo;

    public UsuarioMeResponseDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPsicologoId() {
        return psicologoId;
    }

    public void setPsicologoId(Long psicologoId) {
        this.psicologoId = psicologoId;
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

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
