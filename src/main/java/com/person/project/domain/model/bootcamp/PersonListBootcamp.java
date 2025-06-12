package com.person.project.domain.model.bootcamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonListBootcamp {
    private Long id;
    private String name;
    private String email;
    private List<Bootcamp> bootcamps;
}
