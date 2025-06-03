package com.person.project.infraestructure.entrypoints.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
    private String code;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String param;
    private String message;
}
