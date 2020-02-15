package com.example.demo.task;

public class AccessTokenException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public AccessTokenException(Throwable throwable) {
        super(throwable);
	}
}
