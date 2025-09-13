// Category.java
package com.edugreat.akademiksresource.enums;

public enum Category {
    CRECHE("Creche"),
    PRE_NURSERY("Pre Nursery"),
    NURSERY("Nursery"),
    JUNIOR_PRIMARY("Junior Primary"),
    SENIOR_PRIMARY("Senior Primary"),
    JUNIOR_SECONDARY("Junior Secondary"),
    SENIOR_SECONDARY("Senior Secondary");
   

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
    
    public int getHierarchy() {
    	
    	return this.ordinal()+1;
    	
    }
}