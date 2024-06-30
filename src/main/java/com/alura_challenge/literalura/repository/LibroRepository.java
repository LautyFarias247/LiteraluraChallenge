package com.alura_challenge.literalura.repository;

import com.alura_challenge.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro,Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String tituloLibro);


    @Query(value = "SELECT * FROM libros WHERE :idioma = ANY(idiomas)", nativeQuery = true)
    List<Libro> mostrarLibrosPorIdioma(String idioma);
}
