package com.person.project.infraestructure.entrypoints.personbootcamp.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiPersonBootcampListResponse {
    private String code;
    private String message;
    private String date;
    private List<PersonListBootcampResponse> data;
}
