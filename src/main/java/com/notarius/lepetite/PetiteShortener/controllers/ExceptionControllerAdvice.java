package com.notarius.lepetite.PetiteShortener.controllers;

import com.notarius.lepetite.PetiteShortener.exceptions.BadRequestException;
import com.notarius.lepetite.PetiteShortener.exceptions.NotFoundException;
import com.notarius.lepetite.PetiteShortener.utils.ShortenerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String BadRequestException(final BadRequestException throwable, final Model model) {
        log.error("Something went wrong.", throwable);
        model.addAttribute("errorMessage", ShortenerUtils.BAD_URL);
        return "error";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String KeyNotFound(final NotFoundException throwable, final Model model) {
        log.error("Something went wrong.", throwable);
        model.addAttribute("errorMessage", ShortenerUtils.KEY_NOT_FOUND);
        return "error";
    }
}

