package com.unicap.pi3.service;

import com.unicap.pi3.dto.ProntuarioRequestDTO;
import com.unicap.pi3.dto.ProntuarioResponseDTO;
import com.unicap.pi3.entity.Prontuario;
import com.unicap.pi3.repository.ProntuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;

    private static final List<String> CATALOGO_CID = List.of(
            "F41.1 — Ansiedade generalizada",
            "F41.0 — Transtorno de pânico",
            "F32.0 — Episódio depressivo leve",
            "F32.1 — Episódio depressivo moderado",
            "F43.1 — Estresse pós-traumático",
            "F43.2 — Transtornos de adaptação",
            "F40.1 — Fobias sociais",
            "F90.0 — TDAH",
            "Z73.0 — Esgotamento (Burnout)");

    public ProntuarioService(ProntuarioRepository prontuarioRepository) {
        this.prontuarioRepository = prontuarioRepository;
    }

    @Transactional(readOnly = true)
    public ProntuarioResponseDTO buscarPorPacienteId(Long pacienteId) {
        Prontuario prontuario = prontuarioRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prontuário não encontrado para este paciente."));

        return converterParaDTO(prontuario);
    }

    @Transactional(readOnly = true)
    public ProntuarioResponseDTO buscarPorId(Long id) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prontuário não encontrado."));

        return converterParaDTO(prontuario);
    }

    @Transactional
    public ProntuarioResponseDTO atualizar(Long id, ProntuarioRequestDTO dto) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prontuário não encontrado."));

        prontuario.setAnamnese(dto.getAnamnese());
        prontuario.setObservacoesGerais(dto.getObservacoesGerais());
        prontuario.setCidPrincipal(dto.getCidPrincipal());
        prontuario.setDescricaoCidPrincipal(dto.getDescricaoCidPrincipal());
        prontuario.setCidSecundario(dto.getCidSecundario());
        prontuario.setDescricaoCidSecundario(dto.getDescricaoCidSecundario());
        prontuario.setAtualizadoEm(OffsetDateTime.now());

        Prontuario salvo = prontuarioRepository.save(prontuario);
        return converterParaDTO(salvo);
    }

    public List<String> buscarCid(String termo) {
        if (termo == null || termo.isBlank()) {
            return CATALOGO_CID;
        }
        String filtro = termo.toLowerCase();
        return CATALOGO_CID.stream()
            .filter(cid -> cid.toLowerCase().contains(filtro))
            .toList();
    }

    private ProntuarioResponseDTO converterParaDTO(Prontuario p) {
        return new ProntuarioResponseDTO(
                p.getId(),
                p.getPacienteId(),
                p.getAbertoEm(),
                p.getAnamnese(),
                p.getObservacoesGerais(),
                p.getCidPrincipal(),
                p.getDescricaoCidPrincipal(),
                p.getCidSecundario(),
                p.getDescricaoCidSecundario(),
                p.getAtualizadoEm());
    }
}
