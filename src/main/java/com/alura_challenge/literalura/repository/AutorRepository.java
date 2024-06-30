package com.alura_challenge.literalura.repository;

import com.alura_challenge.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor,Long> {

    Optional<Autor> findByNombre(String nombreAutor);

//    @Query(value = "SELECT a.id AS autor_id, a.nombre, l.id AS libro_id, l.titulo FROM autores a JOIN autor_libro al ON a.id = al.autores_id JOIN libros l ON al.libros_id = l.id",nativeQuery = true)
//    List<Autor> mostrarAutoresGuardados();

    @Query("SELECT a FROM Autor a WHERE a.anioFallecimiento > :anio AND a.anioNacimiento < :anio")
    List<Autor> mostrarAutoresVivosEnAnio(int anio);


}
