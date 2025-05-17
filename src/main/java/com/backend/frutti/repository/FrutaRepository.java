package com.backend.frutti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.frutti.model.Fruta;
@Repository
public interface FrutaRepository extends JpaRepository<Fruta, Long> {

    @Query("SELECT f FROM Fruta f WHERE f.usuario.id = :usuarioId")
    List<Fruta> obtenerHistorialFrutas(@Param("usuarioId") Long usuarioId);

    @Query("SELECT f FROM Fruta f WHERE f.usuario.id = :usuarioId AND f.id = :frutaId")
    Fruta obtenerFruta(@Param("usuarioId") Long usuarioId, @Param("frutaId") Long frutaId);

    void deleteByUsuarioId(Long usuarioId);
}
