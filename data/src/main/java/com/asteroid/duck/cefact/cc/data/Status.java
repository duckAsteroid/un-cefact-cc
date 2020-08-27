package com.asteroid.duck.cefact.cc.data;

import java.util.Optional;

/**
 * Represents known values of the common code status column in the spec table
 */
public enum Status {
    ACTIVE(""),
    ADDED("+"),
    CHANGED_NAME("#"),
    CHANGED_CHARACTERISTIC("|"),
    DEPRECATED("D"),
    DELETED("X"),
    REINSTATED("=");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Parse a code from the table into a status (if possible)
     * @param code the code (null is taken to mean ACTIVE)
     * @return the matching code (case insensitive), or {@link Optional#empty()} if no match found
     */
    public static Optional<Status> forCode(String code) {
        if (code == null) return Optional.of(ACTIVE);
        for(Status s : values()) {
            if (s.code.equalsIgnoreCase(code)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
