package com.person.project.infraestructure.entrypoints.personbootcamp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcampResponse {
    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Integer duration;
}
