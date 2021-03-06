package com.tereshkoff.passwordmanager.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Group implements Serializable {

    private String name;
    private PasswordList passwordList = new PasswordList();
    private Integer imageIndex;

    public Group(String name) {
        this.name = name;
    }

    public Group() { }

    public Group(String name, PasswordList passwordList)
    {
        this.name = name;
        this.passwordList = passwordList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public PasswordList getPasswordList() {
        return passwordList;
    }

    public Integer getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(Integer imageIndex) {
        this.imageIndex = imageIndex;
    }
}
