package com.person.project.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {
    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501","Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    PERSON_CREATED("201", "user created successfully", ""),
    PERSON_LOGGED("200", "you have logged in successfully", ""),
    PERSON_NOT_EXIST("400-1", "Person not found", ""),
    PERSON_BOOTCAMP_CREATED("201-1", "bootcamps associate successfully", ""),

    //bootcamps
    BOOTCAMP_NOT_EXIST("400-1", "Some of bootcamps not found or not exist", ""),
    BOOTCAMP_ONE_ASSOCIATION("404-2", "A person must be associated with at least 1 Bootcamp", ""),
    BOOTCAMP_FIVE_ASSOCIATION("404-3", "A person cannot have more than 5 associated bootcamp", ""),
    BOOTCAMP_DATE_DURATION_DUPLICATED("404-4","Bootcamps with the same date and duration are not allowed.",""),
    BOOTCAMP_DUPLICATES_IDS("400-5","Check the input data, it is trying to save the same bootcamps",""),
    BOOTCAMP_ALREADY_ASSOCIATED("400-6","One or more of the bootcamps are already related to this person",""),
    BOOTCAMP_PERSON_MAX_NUMBER_PERSONS("200","Bootcamp with max number of person found","")
    ;

    private final String code;
    private final String message;
    private final String param;
}
