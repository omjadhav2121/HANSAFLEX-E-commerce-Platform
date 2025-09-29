package com.hansaflex.ecommerce.enums;

public enum Role {
    ADMIN("Administrator"),
    CUSTOMER("Customer");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
