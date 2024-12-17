package com.github.cupangclone.web.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class NotFoundException extends RuntimeException{

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(HttpServletResponse response, String message) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        throw new NotFoundException(message);
    }

}
