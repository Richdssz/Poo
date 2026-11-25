package com.unicap.pi3.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class ProntuarioResponseDTO {

    private Long id;
    private Long pacienteId;
    private LocalDate abertoEm;
    private String anamnese;
    private String observacoesGerais;
    private String cidPrincipal;
    private String descricaoCidPrincipal;
    private String cidSecundario;
    private String descricaoCidSecundario;
    private OffsetDateTime atualizadoEm;

    public ProntuarioResponseDTO() {
    }

    public ProntuarioResponseDTO(
            Long id,
            Long pacienteId,
            LocalDate abertoEm,
            String anamnese,
            String observacoesGerais,
            String cidPrincipal,
            String descricaoCidPrincipal,
            String cidSecundario,
            String descricaoCidSecundario,
            OffsetDateTime atualizadoEm) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.abertoEm = abertoEm;
        this.anamnese = anamnese;
        this.observacoesGerais = observacoesGerais;
        this.cidPrincipal = cidPrincipal;
        this.descricaoCidPrincipal = descricaoCidPrincipal;
        this.cidSecundario = cidSecundario;
        this.descricaoCidSecundario = descricaoCidSecundario;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public LocalDate getAbertoEm() {
        return abertoEm;
    }

    public void setAbertoEm(LocalDate abertoEm) {
        this.abertoEm = abertoEm;
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

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
