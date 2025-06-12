package com.person.project.domain.usecase.personbootcamp.util;

import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.exception.BusinessException;
import com.person.project.infraestructure.adapters.pesistenceadapter.webclient.response.bootcamp.BootcampResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationPersonBootcamp {
    public Mono<List<BootcampResponse>> validateNumberBootcamps(List<BootcampResponse> existingBootcamps, List<BootcampResponse> newBootcamps) {
        int total = existingBootcamps.size() + newBootcamps.size();

        if (total > 5) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_FIVE_ASSOCIATION));
        }

        if (total < 1) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_ONE_ASSOCIATION));
        }

        return Mono.just(existingBootcamps);
    }

    //TODO fecha y duracion entre bootcamps diferentes diferentes
    public Mono<Void> validateUniqueReleaseDateAndDuration(List<BootcampResponse> bootcamps) {
        boolean hasDuplicate = bootcamps.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getReleaseDate() + "-" + b.getDuration(),
                        Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);

        if (hasDuplicate) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_DATE_DURATION_DUPLICATED));
        }

        return Mono.empty();
    }
}
