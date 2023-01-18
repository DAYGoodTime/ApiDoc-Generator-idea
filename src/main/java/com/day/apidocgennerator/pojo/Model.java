package com.day.apidocgennerator.pojo;

import java.io.Serializable;
import java.util.List;

public class Model implements Serializable {
    String name;
    List<Field> fieldList;

    public Model(String name, List<Field> fieldList) {
        this.name = name;
        this.fieldList = fieldList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }
}
