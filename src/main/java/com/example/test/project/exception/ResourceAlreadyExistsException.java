package com.example.test.project.exception;

public class ResourceAlreadyExistsException extends Exception{
    public ResourceAlreadyExistsException() {
    }

    public ResourceAlreadyExistsException(String msg) {
        super(msg);
    }
}
