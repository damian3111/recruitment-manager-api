package com.damian3111.recruitment_manager_api.persistence.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {

    RECRUITER("recruiter"),

    RECRUITED("recruited");
    private final String val;

    UserRole(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
