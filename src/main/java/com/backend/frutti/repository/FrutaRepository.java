package com.backend.frutti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.frutti.model.Fruta;

@Repository
public interface FrutaRepository extends JpaRepository<Fruta, Long> {

    @Modifying
    @Query("UPDATE Fruta f SET f.nombre = :nombre, f.precio = :precio, f.lugarAnalisis = :lugarAnalisis WHERE f.id = :id")
    int actualizarFruta(@Param("id") Long id, @Param("nombre") String nombre, @Param("precio") float precio,
            @Param("lugarAnalisis") String lugarAnalisis);

    @Query("SELECT f FROM Fruta f WHERE f.usuario.id = :usuarioId")
    List<Fruta> obtenerHistorialFrutas(@Param("usuarioId") Long usuarioId);

    @Query("SELECT f FROM Fruta f WHERE f.usuario.id = :usuarioId AND f.id = :frutaId")
    Fruta obtenerFruta(@Param("usuarioId") Long usuarioId, @Param("frutaId") Long frutaId);
}
