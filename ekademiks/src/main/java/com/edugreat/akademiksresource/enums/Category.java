// Category.java
package com.edugreat.akademiksresource.enums;

public enum Category {
    CRECHE("Creche"),
    JUNIOR_PRIMARY("Junior Primary"),
    SENIOR_PRIMARY("Senior Primary"),
    NURSERY("Nursery"),
    PRE_NURSERY("Pre Nursery"),
    SENIOR_SECONDARY("Senior Secondary"),
    JUNIOR_SECONDARY("Junior Secondary");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Category fromLabel(String label) {
        for (Category category : values()) {
        	System.out.println("supplied label: "+label+" given label: "+category.label);
            if (category.label.equals(label)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category label: " + label);
    }
}