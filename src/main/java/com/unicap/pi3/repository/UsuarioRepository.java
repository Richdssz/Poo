
package com.unicap.pi3.repository;

import com.unicap.pi3.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Usuario> findByPsicologoId(Long psicologoId);

    Page<Usuario> findByPapel(String papel, Pageable pageable);

    Page<Usuario> findByAtivo(Boolean ativo, Pageable pageable);

    Page<Usuario> findByPapelAndAtivo(
            String papel,
            Boolean ativo,
            Pageable pageable
    );
}
