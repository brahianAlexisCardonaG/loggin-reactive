package com.person.project.infraestructure.entrypoints.person.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatePersonDto {
    private String email;
    private String password;
}
