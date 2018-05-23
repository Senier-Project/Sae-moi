package com.three_eung.saemoi;

public enum ExCategory {
    CLOTH("의류비"),
    TRANS("교통비"),
    EAT("식비"),
    CULTURE("문화생활비"),
    TELE("통신비"),
    PLAY("유흥비");

    private String title;

    ExCategory(String title) { this.title = title; }

    public String getTitle() { return title; }
}
