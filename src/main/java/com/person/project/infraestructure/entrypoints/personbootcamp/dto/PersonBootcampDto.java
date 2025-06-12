package com.person.project.infraestructure.entrypoints.personbootcamp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonBootcampDto {
    private Long personId;
    private List<Long> bootcampIds;
}
