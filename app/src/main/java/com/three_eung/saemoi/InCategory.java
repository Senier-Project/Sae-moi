package com.three_eung.saemoi;

public enum InCategory {
    ALLOW("용돈"),
    SALARY("급여"),
    INTEREST("이자");

    private String title;

    InCategory(String title) { this.title = title; }

    public String getTitle() { return title; }
}
