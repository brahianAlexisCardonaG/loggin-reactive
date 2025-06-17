package com.person.project.infraestructure.entrypoints.person.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonBasicResponse {
    private Long id;
    private String name;
    private String email;
}
