package io.dymatics.cogny.domain.model;

import java.util.Map;
import java.util.TreeMap;

public enum CognyStatus {
    OK(1000, "OK"),

    DUPLICATED_USER_EMAIL(2001, "Duplicated User Email"),

    DUPLICATED_USER(2002, "Duplicated User"),

    INVALID_ID_OR_PASSWORD(3001, "Invalid Id or Password"),

    INVALID_PREVIOUS_PASSWORD(3002, "Invalid Previous Password"),

    DIFFERENCE_IN_TWO_PASSWORDS(3003, "The Two Passwords Are Different"),

    NOT_FOUND_STAFF_INFO(3004, "Not Found Staff Info"),
    
    INVALID_PARAMETERS(4001, "Invalid Parameters"),
    
    UNAUTHORIZED_PARAMETERS(4002, "Unauthorized Parameters"),

    UNKNOWN_ERROR(9001, "Unknown Error"),

    ACCESS_DENYED(9002, "Access Denyed"),
    
    UNAUTHENTICATED_USER(9003, "Unauthenticated User");

    private final int value;
    private final String message;
    
    CognyStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }
    public int getValue() {
        return this.value;
    }
    public String getMessage() {
        return this.message;
    }
    
    public Map<String, Object> toEntityMap() {
        TreeMap<String, Object> result = new TreeMap<String, Object>();
        result.put("status", this.value);
        result.put("message", this.message);
        return result;
    }
}
