
package com.unicap.pi3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "psicologos", schema = "public")
public class Psicologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "consultorio_id", nullable = false)
    private Short consultorioId = 1;

    @Column(name = "nome", nullable = false, length = 160)
    private String nome;

    @Column(name = "crp", nullable = false, unique = true, length = 32)
    private String crp;

    @Column(name = "telefone", length = 24)
    private String telefone;

    @Column(name = "especialidade", length = 120)
    private String especialidade;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", insertable = false, updatable = false)
    private OffsetDateTime atualizadoEm;

    public Psicologo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(Short consultorioId) {
        this.consultorioId = consultorioId;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
