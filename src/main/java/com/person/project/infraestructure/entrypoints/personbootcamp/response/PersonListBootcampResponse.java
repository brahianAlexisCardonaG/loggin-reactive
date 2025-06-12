package com.person.project.infraestructure.entrypoints.personbootcamp.response;

import com.person.project.domain.model.bootcamp.Bootcamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonListBootcampResponse {
    private Long id;
    private String name;
    private String email;
    private List<BootcampResponse> bootcamps;
}
