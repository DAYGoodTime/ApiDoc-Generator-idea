package com.day.apidocgennerator.pojo;

import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MethodVo {

    private String methodName;

    private String modifier;
    private List<JSONObject> parameterList;

    private String returnType;

    public MethodVo() {
        parameterList = new ArrayList<>();
    }

    public MethodVo(String methodName, String modifier, List<JSONObject> parameterList, String returnType) {
        this.methodName = methodName;
        this.modifier = modifier;
        this.parameterList = parameterList;
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public List<JSONObject> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<JSONObject> parameterList) {
        this.parameterList = parameterList;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

}
