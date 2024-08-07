package com.goldberg.equalexperts.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Data
public class EqualExpertsError {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public EqualExpertsError(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public EqualExpertsError(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(error);
    }

    public EqualExpertsError(String message, String error) {
        super();
        this.message = message;
        this.errors = Arrays.asList(error);
    }
}
