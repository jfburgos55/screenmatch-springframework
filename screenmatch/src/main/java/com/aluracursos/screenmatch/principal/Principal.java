package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=91641455";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraMenu(){
        System.out.println("Por favor escribe el nombre de la serie a buscar: ");
        String nombreSerie = teclado.nextLine();
        String url;
        var json = consumoAPI.obtenerDatos(url= URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        // Busca datos de todas las temporadas
        System.out.println("- Temporadas ");
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <=  datos.totalTemporadas(); i++) {
            String urlTemporadas = URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY;
            json=consumoAPI.obtenerDatos(url = urlTemporadas);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
        temporadas.forEach(System.out::println);

        // Mostrar solo el titulo de los episodios para la temporada
        /*
        for (int i = 0; i < datos.totalTemporadas() ; i++) {
            List<DatosEpisodio> episodioTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodioTemporada.size(); j++) {
                System.out.println("Nombre: " + episodioTemporada.get(j).Titulo());
            }
        }*/

        temporadas.forEach(datosTemporadasL -> datosTemporadasL.episodios().forEach(datosEpisodioL -> System.out.println(datosEpisodioL.Titulo())));

        // Convertir todas las informaciones a una lista del tipo DatosEpisodio
        List<DatosEpisodio> datosEpisodiosL = temporadas.stream()
                .flatMap(temporadasList -> temporadasList.episodios().stream())
                .collect(Collectors.toList());

        // Top 5 episodioss
        System.out.println("- Top 5 Episodios");
        datosEpisodiosL.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primer filtro (N/A)" + e ))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .peek(e -> System.out.println("Segundo Ordenacion (M>m)" + e ))
                .map(e -> e.Titulo().toUpperCase())
                .peek(e -> System.out.println("Tercer Filtro Mayúscula (m>M)" + e ))
                .limit(5)
                .forEach(System.out::println);

        //Convirtiendo los datos a una lista del tipo episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(temporadaL -> temporadaL.episodios().stream()
                .map(datosEpisodioL -> new Episodio(temporadaL.numero(), datosEpisodioL)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        //Búsquedas de episodio a partir de un x año
        System.out.println("\n[INFO] - Introduce el año a partir del cual deseas ver los episodios: ");
        var fecha = teclado.nextInt();
        teclado.nextLine();

        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        // Formateador fecha
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getFechaDeLanzamiento() != null &&  e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                "\tEpisodio: " + e.getTitulo() +
                                "\tFecha de lanzamiento: " + e.getFechaDeLanzamiento().format(dtf)
                ));

//        Busca episodio por un pedazo de titulo
//        System.out.println("[INFO] - Digita el titulo del episodio que deseas ver: ");
//        var pedazoTitulo = teclado.nextLine();
//        Optional<Episodio> epissodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (epissodioBuscado.isPresent()){
//            System.out.println("[INFO] - Episodio encontrado");
//            System.out.println("[INFO] - Los datos son: " + epissodioBuscado.get());
//        } else {
//            System.out.println("[ERROR] - Episodio no encontrado");
//        }

        // Calcular estadistica de los episodios
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(episodioL -> episodioL.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));

        System.out.println(evaluacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(episodioL -> episodioL.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));

        System.out.println("Media de las evalidaciones: " + est.getAverage());
        System.out.println("Episodio Mejor Evaluado: " + est.getMax());
        System.out.println("Episodio Peor Evaluado: " + est.getMin());
    }
}
