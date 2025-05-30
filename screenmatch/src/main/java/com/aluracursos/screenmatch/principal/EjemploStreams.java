package com.aluracursos.screenmatch.principal;

import java.util.Arrays;
import java.util.List;

public class EjemploStreams {

    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Brenda", "Luis", "Genesis","Eric","Roberto","Carlos");

        nombres.stream()
                .sorted()
                .limit(4)
                .filter(nombre -> nombre.startsWith("G"))
                .map(nombre -> nombre.toUpperCase())
                .forEach(System.out::println);
    }
}
