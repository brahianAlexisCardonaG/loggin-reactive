package com.person.project.infraestructure.entrypoints.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String X_MESSAGE_ID = "x-message-id";
    public static final String PERSON_ERROR = "Error on Person - [ERROR]";

    //person
    public static final String PATH_AUTH_REGISTER_PERSON
            = "/api/v1/auth/register";
    public static final String PATH_AUTH_AUTHENTICATION_PERSON
            = "/api/v1/auth/authenticate";

    //person-bootcamp
    public static final String PATH_PERSON_BOOTCAMP_CREATE
            = "/api/v1/person/bootcamp";
    public static final String PATH_BOOTCAMP_PERSON_GET_MAX_NUMBER_PERSON
            = "/api/v1/person/bootcamp/get-bootcamp-person";

}
