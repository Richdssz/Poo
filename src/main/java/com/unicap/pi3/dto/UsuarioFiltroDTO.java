
package com.unicap.pi3.dto;

public class UsuarioFiltroDTO {

    private String papel;
    private Boolean ativo;

    public UsuarioFiltroDTO() {}

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
