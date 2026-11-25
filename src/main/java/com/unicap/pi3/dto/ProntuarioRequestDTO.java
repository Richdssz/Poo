package com.unicap.pi3.dto;

public class ProntuarioRequestDTO {

    private String anamnese;
    private String observacoesGerais;
    private String cidPrincipal;
    private String descricaoCidPrincipal;
    private String cidSecundario;
    private String descricaoCidSecundario;

    public ProntuarioRequestDTO() {
    }

    public String getAnamnese() {
        return anamnese;
    }

    public void setAnamnese(String anamnese) {
        this.anamnese = anamnese;
    }

    public String getObservacoesGerais() {
        return observacoesGerais;
    }

    public void setObservacoesGerais(String observacoesGerais) {
        this.observacoesGerais = observacoesGerais;
    }

    public String getCidPrincipal() {
        return cidPrincipal;
    }

    public void setCidPrincipal(String cidPrincipal) {
        this.cidPrincipal = cidPrincipal;
    }

    public String getDescricaoCidPrincipal() {
        return descricaoCidPrincipal;
    }

    public void setDescricaoCidPrincipal(String descricaoCidPrincipal) {
        this.descricaoCidPrincipal = descricaoCidPrincipal;
    }

    public String getCidSecundario() {
        return cidSecundario;
    }

    public void setCidSecundario(String cidSecundario) {
        this.cidSecundario = cidSecundario;
    }

    public String getDescricaoCidSecundario() {
        return descricaoCidSecundario;
    }

    public void setDescricaoCidSecundario(String descricaoCidSecundario) {
        this.descricaoCidSecundario = descricaoCidSecundario;
    }
}
