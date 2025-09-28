package com.SriLankaCard.entity.enums;

public enum UserStatus {
    ATIVO("User Active"),
    INATIVO("User Inactive");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
