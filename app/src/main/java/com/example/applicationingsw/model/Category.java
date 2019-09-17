package com.example.applicationingsw.model;

public class Category {
    private int id;
    private String name;
    private String description;
    private int superCategory;

    public Category(int anId, String aName, String aDescription, int aSuperCategory){
        id = anId;
        name = aName;
        description = aDescription;
        superCategory = aSuperCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSuperCategory() {
        return superCategory;
    }

    public void setSuperCategory(int superCategory) {
        this.superCategory = superCategory;
    }
}
