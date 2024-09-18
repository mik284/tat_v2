package com.company.tathminiv2.rest.exeptions;

public class ItemAlreadyExistsExeption extends RuntimeException {
    public ItemAlreadyExistsExeption(String message) {
        super(message);
    }
}