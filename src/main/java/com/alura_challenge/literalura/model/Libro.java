package com.alura_challenge.literalura.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true)
    private String titulo;
    private List<String> idiomas;
    @ManyToMany(mappedBy = "libros",fetch = FetchType.EAGER)
    private List<Autor> autores;
    private Integer descargas;

    public Libro(){}

    public Libro(DatosLibro datosLibro){
        this.titulo = datosLibro.titulo();
        this.idiomas = datosLibro.idiomas();
        this.descargas = datosLibro.descargas();

    }

    public void setAutores(List<DatosAutor> datosAutores) {
        List<Autor> autores = datosAutores.stream().map((a)-> new Autor(a)).collect(Collectors.toList());
        this.autores = autores;
    }

    @Override
    public String toString() {
        String autores = this.autores.stream().map(Autor::getNombre)
                .collect(Collectors.joining(", "));

          return "Título: " + this.titulo + "\n" +
                "Autor: " + autores + "\n" +
                "Idiomas: " + this.idiomas + "\n" +
                "Descargas: " + this.descargas;
    }



    public Integer getDescargas() {
        return descargas;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }
}
