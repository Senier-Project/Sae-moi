package com.three_eung.saemoi.infos;

public class BudgetInfo {
    private String category;
    private Integer value;

    public BudgetInfo() {}
    public BudgetInfo(String category) {
        this.category = category;
    }
    public BudgetInfo(String category, int value) {
        this.category = category;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }
}
