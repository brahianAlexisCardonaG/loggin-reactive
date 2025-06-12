package com.person.project.domain.model.bootcamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bootcamp {
    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Integer duration;
}
