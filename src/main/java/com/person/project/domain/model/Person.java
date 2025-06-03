package com.person.project.domain.model;

import com.person.project.domain.enums.RoleUserEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private Long id;
    private String name;
    private String email;
    private String age;
    private String password;
    private RoleUserEnum roleUserEnum;
}
