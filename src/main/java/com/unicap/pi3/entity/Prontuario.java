package com.unicap.pi3.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "prontuarios")
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false, unique = true)
    private Long pacienteId;

    @Column(name = "aberto_em", nullable = false)
    private LocalDate abertoEm = LocalDate.now();

    @Column(columnDefinition = "TEXT")
    private String anamnese;

    @Column(name = "observacoes_gerais", columnDefinition = "TEXT")
    private String observacoesGerais;

    @Column(name = "cid_principal", length = 20)
    private String cidPrincipal;

    @Column(name = "descricao_cid_principal")
    private String descricaoCidPrincipal;

    @Column(name = "cid_secundario", length = 20)
    private String cidSecundario;

    @Column(name = "descricao_cid_secundario")
    private String descricaoCidSecundario;

    @Column(name = "criado_em", updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "atualizado_em")
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    public Prontuario() {
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

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
