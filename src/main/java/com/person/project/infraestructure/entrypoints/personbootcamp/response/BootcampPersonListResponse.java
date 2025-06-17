package com.person.project.infraestructure.entrypoints.personbootcamp.response;

import com.person.project.domain.model.person.PersonBasic;
import com.person.project.infraestructure.entrypoints.person.response.PersonBasicResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BootcampPersonListResponse {
    private Long idBootcamp;
    private String name;
    private LocalDate releaseDate;
    private Integer duration;
    private List<PersonBasicResponse> persons;
}
