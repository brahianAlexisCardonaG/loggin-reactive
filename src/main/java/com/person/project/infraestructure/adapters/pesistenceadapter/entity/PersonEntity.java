package com.person.project.infraestructure.adapters.pesistenceadapter.entity;

import com.person.project.domain.enums.RoleUserEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("person")
public class PersonEntity {

    @Id
    private Long id;
    private String name;
    private String email;
    private String age;
    private String password;
    @Column(value="role_user_enum")
    private RoleUserEnum roleUserEnum;
}
