package com.person.project.infraestructure.adapters.pesistenceadapter.personbootcamp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "bootcamp_person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonBootcampEntity {

    @Id
    private Long id;

    @Column("id_person")
    private Long idPerson;

    @Column("id_bootcamp")
    private Long idBootcamp;
}
