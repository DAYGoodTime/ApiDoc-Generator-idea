package com.day.apidocgennerator.pojo;

import cn.hutool.core.annotation.Alias;

import java.util.List;


public class Result_model {
    @Alias("modelName")
    private String modelName;
    @Alias("fieldList")
    private List<Result_Field> fieldList;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<Result_Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Result_Field> fieldList) {
        this.fieldList = fieldList;
    }
}
