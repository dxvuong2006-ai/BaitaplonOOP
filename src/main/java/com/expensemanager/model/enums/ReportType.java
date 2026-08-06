package com.expensemanager.model.enums;

public enum ReportType {
    EXCEL("EXCEL"),
    PDF("PDF");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
