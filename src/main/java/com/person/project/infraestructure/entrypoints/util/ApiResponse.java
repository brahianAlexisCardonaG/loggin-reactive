package com.person.project.infraestructure.entrypoints.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.person.project.domain.model.AuthenticationResponse;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiResponse {
    private String code;
    private String message;
    private String identifier;
    private String date;
    private AuthenticationResponse data;
    private List<ErrorDto> errors;
}
