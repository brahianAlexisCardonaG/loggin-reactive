package com.person.project.infraestructure.entrypoints.person.dto;

import com.person.project.domain.enums.RoleUserEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPersonDto {
    private Long id;
    private String name;
    private String email;
    private String age;
    private String password;
    private RoleUserEnum roleUserEnum;
}
