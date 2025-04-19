package com.backend.frutti.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.frutti.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.email = :email, u.nombre = :nombre, u.edad = :edad WHERE u.id = :id")
    int actualizarUsuario(@Param("id") Long id, @Param("email") String email, @Param("nombre") String nombre,
            @Param("edad") int edad);

}
