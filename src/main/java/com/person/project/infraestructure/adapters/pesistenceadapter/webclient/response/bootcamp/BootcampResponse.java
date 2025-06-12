package com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.bootcamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BootcampResponse {
    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Integer duration;
}
