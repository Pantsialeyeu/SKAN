package com.tkmi.serialization;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.Map;

public class RequestProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private Object message;

    public RequestProperties(String key, Object message) {
        this.key = key;
        this.message = message;
    }

    public RequestProperties() {

    }

    public Object getKey() {
        return key;
    }

    public Object getMessage() {
        return message;
    }

}
