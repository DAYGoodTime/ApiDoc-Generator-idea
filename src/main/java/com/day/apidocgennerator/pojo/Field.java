package com.day.apidocgennerator.pojo;

import java.io.Serializable;

public class Field implements Serializable {

    String name;
    String dataType;

    public Field(String name, String type) {
        this.name = name;
        this.dataType = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
