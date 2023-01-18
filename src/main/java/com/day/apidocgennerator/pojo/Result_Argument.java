package com.day.apidocgennerator.pojo;

import cn.hutool.core.annotation.Alias;

import java.io.Serializable;
public class Result_Argument implements Serializable {
    @Alias("name")
    private String name;
    @Alias("description")
    private String description;
    @Alias("example")
    private String example;
    @Alias("isRequired")
    private boolean isRequired;
    @Alias("dataType")
    private String dataType;
    @Alias("elementName")
    private String elementName;
    @Alias("subType")
    private String subType;
    @Alias("isObject")
    private boolean isObject;
    @Alias("isArray")
    private boolean isArray;

    @Alias("isPath")
    private boolean isPath;

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

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public boolean isObject() {
        return isObject;
    }

    public void setObject(boolean object) {
        isObject = object;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public boolean isPath() {
        return isPath;
    }

    public void setPath(boolean path) {
        isPath = path;
    }
}
