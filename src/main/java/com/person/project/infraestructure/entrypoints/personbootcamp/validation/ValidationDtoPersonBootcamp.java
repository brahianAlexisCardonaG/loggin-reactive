package com.person.project.infraestructure.entrypoints.personbootcamp.validation;

import com.person.project.domain.enums.TechnicalMessage;
import com.person.project.domain.exception.BusinessException;
import com.person.project.infraestructure.entrypoints.personbootcamp.dto.PersonBootcampDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidationDtoPersonBootcamp {
    public Mono<PersonBootcampDto> validateDuplicateIds(PersonBootcampDto personBootcampDto) {
        Set<Long> ids = new HashSet<>();
        List<Long> duplicatedIds = personBootcampDto.getBootcampIds().stream()
                .filter(id -> !ids.add(id)) // Si no se puede agregar al set, es duplicado
                .toList();

        if (!duplicatedIds.isEmpty()) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_DUPLICATES_IDS));
        }

        return Mono.just(personBootcampDto);
    }

    public Mono<PersonBootcampDto> validateFieldNotNullOrBlank(PersonBootcampDto dto) {
        if (dto.getPersonId() == null || dto.getBootcampIds() == null || dto.getBootcampIds().isEmpty()) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }
}
