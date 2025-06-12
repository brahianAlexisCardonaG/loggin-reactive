package com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.api;

import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.bootcamp.BootcampResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiBootcampListResponse {
    private String code;
    private String message;
    private String date;
    List<BootcampResponse> data;
}
