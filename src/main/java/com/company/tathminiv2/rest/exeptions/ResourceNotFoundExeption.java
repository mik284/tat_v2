package com.company.tathminiv2.rest.exeptions;

public class ResourceNotFoundExeption extends RuntimeException {

    public ResourceNotFoundExeption(String message){
        super(message);
    }
}
