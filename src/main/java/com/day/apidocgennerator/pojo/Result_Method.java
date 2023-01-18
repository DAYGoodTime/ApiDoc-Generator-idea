package com.day.apidocgennerator.pojo;

import cn.hutool.core.annotation.Alias;

import java.io.Serializable;
import java.util.List;

public class Result_Method implements Serializable {
    @Alias("methodName")
    private String methodName;
    @Alias("RequestBody")
    private List<Result_Argument> requestBody;
    @Alias("normalArgument")
    private List<Result_Argument> normalArgument;
    @Alias("Response")
    private List<Result_Argument> Response;
    @Alias("methodSummary")
    private String methodSummary;
    @Alias("methodDescription")
    private String methodDescription;

    @Alias("isRequestBody")
    private boolean isRequestBody;

    public List<Result_Argument> getResponse() {
        return Response;
    }

    public void setResponse(List<Result_Argument> response) {
        Response = response;
    }

    public boolean isRequestBody() {
        return isRequestBody;
    }

    public void setRequestBody(boolean requestBody) {
        isRequestBody = requestBody;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Result_Argument> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(List<Result_Argument> requestBody) {
        this.requestBody = requestBody;
    }

    public List<Result_Argument> getNormalArgument() {
        return normalArgument;
    }

    public void setNormalArgument(List<Result_Argument> normalArgument) {
        this.normalArgument = normalArgument;
    }

    public String getMethodSummary() {
        return methodSummary;
    }

    public void setMethodSummary(String methodSummary) {
        this.methodSummary = methodSummary;
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }
}
