package com.aluracursos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DatosSerie(
    @JsonAlias("Title") String Titulo,
    @JsonAlias("totalSeasons") Integer totalTemporadas,
    @JsonAlias("imdbRating") String evaluacion ){

    //@JsonAlias - @JsonProperty - Diferencia alias lee, property permite escribir

}
