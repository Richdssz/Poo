package com.unicap.pi3.repository;

import com.unicap.pi3.entity.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    Optional<Prontuario> findByPacienteId(Long pacienteId);
}
