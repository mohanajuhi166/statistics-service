package com.events.statsservice.model;

import java.io.Serializable;


public class Error implements Serializable {
	   
    private static final long serialVersionUID = -6763449483498359995L;
    
    private String code;
    private String message;
    private String target;
    private Error[] details;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Error[] getDetails() {
        return details;
    }

    public void setDetails(Error[] details) {
        this.details = details;
    }
}
