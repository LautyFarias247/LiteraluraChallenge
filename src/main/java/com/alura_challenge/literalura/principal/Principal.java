package com.alura_challenge.literalura.principal;

import com.alura_challenge.literalura.model.Autor;
import com.alura_challenge.literalura.model.DatosBusquedaLibro;
import com.alura_challenge.literalura.model.DatosLibro;
import com.alura_challenge.literalura.model.Libro;
import com.alura_challenge.literalura.repository.AutorRepository;
import com.alura_challenge.literalura.repository.LibroRepository;
import com.alura_challenge.literalura.service.ConsumoAPI;
import com.alura_challenge.literalura.service.ConvierteDatos;
import org.hibernate.engine.internal.Collections;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private DatosBusquedaLibro datosBusquedaLibro;
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<Libro> libros;
    private List<Autor> autores;

    public Principal(LibroRepository libroRepository,AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }


    public void muestraElMenu(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    ***************************************************************
                    1) Buscar libro por título
                    2) Listar libros registrados
                    3) Listar autores registrados
                    4) Listar autores vivos en un determinado año
                    5) Listar libros por idioma
                    
                    0) Salir
                    ***************************************************************
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    mostrarLibrosGuardados();
                    break;
                case 3:
                    mostrarAutoresGuardados();
                    break;
                case 4:
                    mostrarAutoresVivosEnAnio();
                    break;
                case 5:
                    mostrarLibrosPorIdioma();
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        };
    }
    private DatosBusquedaLibro getLibro(){
        System.out.println("Escriba el titulo del libro que desea buscar:");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE+tituloLibro.replace(" ","+"));
        System.out.println(json);
        return conversor.obtenerDatos(json,DatosBusquedaLibro.class);
    };

    public void buscarLibro(){
        DatosBusquedaLibro datosBusqueda = getLibro();

        if(!datosBusqueda.libros().isEmpty()){
            DatosLibro datosLibro = datosBusqueda.libros().get(0);

            Optional<Libro> libroExistente = libroRepository.findByTituloContainsIgnoreCase(datosLibro.titulo());

            if (libroExistente.isPresent()) {
                System.out.println("El libro ya se encuentra guardado!");
                return;
            }

            Libro libro = new Libro(datosLibro);
            try {
                libroRepository.save(libro);
            } catch (RuntimeException e) {
                System.out.println("Error al guardar el libro!");
                return;
            }

            List<Autor> autores = datosLibro.autores().stream()
                    .map(a -> {
                        Optional<Autor> autorExistente = autorRepository.findByNombre(a.nombre());
                        Autor autor;
                        if (autorExistente.isPresent()) {
                            autor = autorExistente.get();
                            autor.getLibros().add(libro);
                        } else {
                            autor = new Autor(a);
                            autor.setLibros(List.of(libro));
                        }
                        return autor;
                    })
                    .collect(Collectors.toList());

            try {
                autorRepository.saveAll(autores);
            } catch (RuntimeException e) {
                System.out.println("Error al guardar los autores!");
            }

        }else{
            System.out.println("El libro buscado no se encuentra en la base de datos");
        }

//        if(!datosBusqueda.libros().isEmpty()){
//
//
//
//
//            try {
//                libroRepository.save(libro);
//            }catch (DataIntegrityViolationException e){
//                System.out.println("El libro ya se encuentra guardado!");
//                return;
//            }
//            List<Libro> listaLibro = new ArrayList<>();
//            listaLibro.add(libro);
//            List<Autor> autores = datosLibro.autores().stream().map(a->new Autor(a)).toList();
//            autores.forEach(a->a.setLibros(listaLibro));
//            System.out.println(autores);
//            try{
//                autorRepository.saveAll(autores);
//            }catch (DataIntegrityViolationException e){
//                System.out.println("El autor ya se encuentra guardado");
//            }
//        }
    };

    private void mostrarLibrosGuardados() {
        libros = libroRepository.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getDescargas))
                .forEach(l->{
                            System.out.println(l);
                            System.out.println("-----------------------------");
                        });
    }

    private void mostrarAutoresGuardados(){
        autores = autorRepository.findAll();


        System.out.println("--------------AUTORES ENCONTRADOS --------------");
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(a -> {
                    System.out.println(a);
                    System.out.println("Libros:");
                    a.getLibros().forEach(l->{
                        System.out.println("- "+l.getTitulo());
                    });
                    System.out.println("++++++++++++++++++++++++++");
                });
    }

    private void mostrarAutoresVivosEnAnio(){
        System.out.println("Escribe un año para buscar los autores vivos durante esa fecha");
        var anio = teclado.nextInt();
        teclado.nextLine();
        System.out.println(anio);

        List<Autor> autores = autorRepository.mostrarAutoresVivosEnAnio(anio);

        if(autores.isEmpty()){
            System.out.println("No se encontraron autores vivos durante ese año");
        }else{
            autores.forEach(System.out::println);
        }
    }

    private void mostrarLibrosPorIdioma(){
        System.out.println("""
                Elige el codigo de idioma en que quieres ver libros
                es - español
                en - ingles
                pt - portugues
                fr - frances
                """);
        var idioma = teclado.nextLine();
        System.out.println(idioma);
        List<String> idiomasValidos = List.of("es", "en", "pt","fr");
        if(idiomasValidos.contains(idioma)){
            List<Libro> libros = libroRepository.mostrarLibrosPorIdioma(idioma);

            if(libros.isEmpty()){
                System.out.println("No se encontraron libros registrados en el idioma seleccionado");
            }else{
                libros.forEach(System.out::println);
            }

        }else{
            System.out.println("Elige un idioma válido");
        }
    }
}
