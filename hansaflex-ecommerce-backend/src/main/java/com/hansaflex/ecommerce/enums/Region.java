package com.hansaflex.ecommerce.enums;

public enum Region {
    EU("Europe"),
    APAC("Asia Pacific"),
    US("United States");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
