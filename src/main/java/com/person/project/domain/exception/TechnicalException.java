package com.person.project.domain.exception;

import com.person.project.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends ProcessorException{

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }

    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }
}
