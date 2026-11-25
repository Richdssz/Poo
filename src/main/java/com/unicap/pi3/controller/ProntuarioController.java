package com.unicap.pi3.controller;

import com.unicap.pi3.dto.ProntuarioRequestDTO;
import com.unicap.pi3.dto.ProntuarioResponseDTO;
import com.unicap.pi3.service.ProntuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @GetMapping("/prontuarios/paciente/{pacienteId}")

    public ResponseEntity<ProntuarioResponseDTO> buscarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(prontuarioService.buscarPorPacienteId(pacienteId));
    }

    @GetMapping("/prontuarios/{id}")

    public ResponseEntity<ProntuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prontuarioService.buscarPorId(id));
    }

    @PutMapping("/prontuarios/{id}")
    public ResponseEntity<ProntuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProntuarioRequestDTO dto) {
        return ResponseEntity.ok(prontuarioService.atualizar(id, dto));
    }

    @GetMapping("/cid")
    public ResponseEntity<List<String>> buscarCid(
            @RequestParam(required = false, defaultValue = "") String termo) {
        return ResponseEntity.ok(prontuarioService.buscarCid(termo));
    }
}
