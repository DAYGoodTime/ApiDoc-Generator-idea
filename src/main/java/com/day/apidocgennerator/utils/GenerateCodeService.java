package com.day.apidocgennerator.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.day.apidocgennerator.pojo.Result_Argument;
import com.day.apidocgennerator.pojo.Result_Field;
import com.day.apidocgennerator.pojo.Result_Method;
import com.day.apidocgennerator.pojo.Result_model;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateCodeService {
    JSONObject json;

    String ProjectPath;

    PsiJavaFile curFile;

    PsiClass curClass;

    public GenerateCodeService(JSONObject json, String ProjectPath, PsiJavaFile curFile) {
        this.json = json;
        this.ProjectPath = ProjectPath + "generator";
        this.curFile = curFile;
        this.curClass = curFile.getClasses()[0];
    }


    public void Start() {
        if(FileUtil.isDirectory(ProjectPath)) FileUtil.del(ProjectPath);
        if (json == null || json.isEmpty()) return;
        if (!json.isNull("methods") && !json.isNull("models")) {
            loadShames();//加载model
            List<Result_Method> methodList = json.getBeanList("methods", Result_Method.class);
            String packageStr = "package " + (curFile.getPackageName())+";";
            String importList = (curFile.getImportList().getText()) + "\n" +
                    "import io.swagger.v3.oas.annotations.Operation;\n" +
                    "import io.swagger.v3.oas.annotations.Parameter;\n" +
                    "import io.swagger.v3.oas.annotations.enums.ParameterIn;\n" +
                    "import io.swagger.v3.oas.annotations.media.Content;\n" +
                    "import io.swagger.v3.oas.annotations.media.Schema;\n" +
                    "import io.swagger.v3.oas.annotations.responses.ApiResponse;\n" +
                    "import io.swagger.v3.oas.annotations.tags.Tag;";
            String methodModify = curClass.getModifierList().getText();
            StringBuilder AllField = new StringBuilder();
            for (PsiField field : curClass.getAllFields()) {
                String fieldStr = field.getText();
                AllField.append(fieldStr).append("\n");
            }
            String methods = loadMethod(methodList); //加载方法
            String ClassName = curClass.getName();
            Map<String,String> data = new HashMap<>();
            data.put("ClassName",ClassName);
            data.put("package",packageStr);
            data.put("import",importList);
            data.put("fields",AllField.toString());
            data.put("methodModify",methodModify);
            data.put("methods",methods);
            GenerateJavaPojo.GenerateClass(data,ProjectPath,ClassName);
        }
    }

    public String loadMethod(List<Result_Method> methodList) {
        String OperationTemplate =
                "@Operation(summary = \"{summary}\",description = \"{description}\",\n" + "{requestBody}" + "{parameters}" +
                        "\n" +
                        "            \n" +
                        "            responses = {\n" +
                        "                    @ApiResponse(responseCode = \"200\",description = \"操作成功,并返回数据(如果有)\",content = @Content(mediaType = \"application/json\",schema = @Schema(implementation = {}))),\n" +
                        "                    @ApiResponse(responseCode = \"100\",description = \"操作失败\",content = @Content(mediaType = \"application/json\",schema = @Schema(implementation = {})))\n" +
                        "            }\n" +
                        "    )";
        String RequestBodyTemplate = "            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(\n" +
                "                    required = true,\n" +
                "                    content = @Content(\n" +
                "                            mediaType = \"application/json\",\n" +
                "                            schema = @Schema(implementation = {})\n" +
                "                    ))\n,";
        Map<String, Result_Method> methodMap = new HashMap<>(methodList.size());
        StringBuffer result = new StringBuffer();
        for (Result_Method method : methodList) {
            methodMap.put(method.getMethodName(), method);
        }
        for (PsiMethod method : curClass.getMethods()) {
            String context = method.getText();
            String name = method.getName();
            Result_Method rm = methodMap.get(name);
            if(rm==null) continue;
            String summary = rm.getMethodSummary();
            String description = rm.getMethodDescription();
            Map<String,Object> formatMap = new HashMap<>();
            formatMap.put("summary",summary);
            formatMap.put("description",description);
            if (rm.isRequestBody()) {
                loadRequestBody(rm.getRequestBody(), name,false);//loadReqBody
                loadResponse(rm.getResponse(),name);//load Resp
                String requestBody = StrUtil.format(RequestBodyTemplate, name + "Req.class");
                //TODO RequestBody实体类
                formatMap.put("requestBody",requestBody);
            } else {//无RequestBody
                formatMap.put("requestBody","");
                //TODO 返回参数
            }
            if(!isEmptyArgument(rm.getNormalArgument())){
                formatMap.put("parameters",loadNormalArgument(rm.getNormalArgument()));
            }else {
                formatMap.put("parameters","");
            }
            result.append(StrUtil.format(OperationTemplate, formatMap)).append(context);
        }
        return result.toString();
    }

    // @Schema(name = "pictures",description = "图片文件集合",type = "array",required = true)
    public void loadShames() {
        List<Result_model> modelList = json.getBeanList("models", Result_model.class);
        for (Result_model rm : modelList) {
            if(rm.getModelName().isBlank()) continue;
            StringBuffer arguments = new StringBuffer();
            Map<String, String> data = new HashMap<>();
            data.put("name", rm.getModelName());
            String[] split = curFile.getPackageName().split("[.]");
            StringBuilder packageName = new StringBuilder();
            packageName.append("com");
            for (int i = 1; i < split.length - 1; i++) {
                packageName.append(".").append(split[i]);
            }
            packageName.append(".generator.schemas");
            data.put("package", packageName.toString());
            //TODO 判断model是否存在
            List<Result_Field> fieldList = rm.getFieldList();
            String SchemaTemplate = "\n     @Schema(name = \"{name}\",description = \"{description}\",type = \"{dataType}\" {subType} ,required = {isRequire}, example = \"{example}\" {implementation})\n" +
                    "   private {type} {name};";
            for (Result_Field field : fieldList) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", field.getName());
                map.put("description", field.getDescription());
                map.put("type", field.getDataType());
                map.put("example", field.getExample());
                map.put("isRequire", field.isRequired());
                if (field.isArray()) {
                    map.put("dataType", "array");
                    map.put("subType", ", subTypes = " + field.getSubType());
                } else if (field.isObject()) {
                    map.put("dataType", "object");
                    map.put("implementation", ", implementation = " + field.getElementName());
                    map.put("subType", "");
                } else {
                    map.put("dataType", "string");
                    map.put("implementation", "");
                    map.put("subType", "");
                }
                String fileResult = StrUtil.format(SchemaTemplate, map);
                arguments.append(fileResult);
            }
            data.put("arguments", arguments.toString());
            GenerateJavaPojo.Generate(data,ProjectPath,rm.getModelName());
        }
    }

    private void loadResponse(List<Result_Argument> responseList,String methodName){
        loadRequestBody(responseList,methodName,true);
    }

    public void loadRequestBody(List<Result_Argument> requestBody, String methodName,boolean isResp) {
        StringBuffer arguments = new StringBuffer();
        Map<String, String> data = new HashMap<>();
        String name;
        if(isResp) name = methodName + "Resp";
        else name = methodName + "Req";
        data.put("name",name);
        String[] split = curFile.getPackageName().split("[.]");
        StringBuilder packageName = new StringBuilder();
        for (int i = 1; i < split.length - 1; i++) {
            packageName.append(".").append(split[i]);
        }
        packageName.append(".generator.schemas");
        data.put("package", packageName.toString());
        for (Result_Argument argument : requestBody) {
            String SchemaTemplate = "\n     @Schema(name = \"{name}\",description = \"{description}\",type = \"{dataType}\" {subType} ,required = {isRequire}, example = \"{example}\" {implementation})\n" +
                    "   private {type} {name};";
            Map<String, Object> map = new HashMap<>();
            if(argument.getName().isBlank()) continue;
            map.put("name", argument.getName());
            map.put("description", argument.getDescription());
            map.put("type", argument.getDataType());
            map.put("example", argument.getExample());
            map.put("isRequire", argument.isRequired());
            if (argument.isArray()) {
                map.put("dataType", "array");
                map.put("subType", ", subTypes = " + argument.getSubType());
            } else if (argument.isObject()) {
                map.put("dataType", "object");
                map.put("implementation", ", implementation = " + argument.getElementName());
                map.put("subType", "");
            } else {
                map.put("dataType", "string");
                map.put("implementation", "");
                map.put("subType", "");
            }
            String fileResult = StrUtil.format(SchemaTemplate, map);
            arguments.append(fileResult);
        }
        data.put("arguments", arguments.toString());
        GenerateJavaPojo.Generate(data,ProjectPath,name);
    }

    //
    public String loadNormalArgument(List<Result_Argument> arguments) {
        String ArgumentTemplate = "\n                parameters = {\n" +
                "                    {parameters}\n" +
                "            },";
        Map<String, String> temp = new HashMap<>(1);
        String ParameterTemplate = "@Parameter(name = \"{name}\", description = \"{description}\", required = {isRequire},in = {isPath}, example = \"{example}\")\n";
        StringBuffer parameters = new StringBuffer();
        for (Result_Argument argument : arguments) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", argument.getName());
            map.put("description", argument.getDescription());
            map.put("type", argument.getDataType());
            map.put("example", argument.getExample());
            map.put("isRequire", argument.isRequired());
            if (argument.isPath()) map.put("isPath", "ParameterIn.PATH");
            else map.put("isPath", "ParameterIn.QUERY");
            parameters.append(StrUtil.format(ParameterTemplate, map));
        }
        temp.put("parameters", parameters.toString());
        return StrUtil.format(ArgumentTemplate, temp);
    }

    public static boolean isEmptyArgument(List<Result_Argument> argumentList){
        if(argumentList.isEmpty()) return true;
        return argumentList.get(0).getName().isBlank();
    }
}
