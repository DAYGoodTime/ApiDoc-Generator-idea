package com.day.apidocgennerator.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;

import java.util.Map;

public class GenerateJavaPojo {

    public static void Generate(Map<String,String> data,String PATH,String name){
        String filePath = PATH + "/schemas/" +name+ ".java";
        FileWriter writer = new FileWriter(FileUtil.touch(filePath));
        writer.write(StrUtil.format(javaPojoTemplate,data));
    }

    public static void GenerateClass(Map<String,String> data,String PATH,String name){
        String filePath = PATH + "/controller/" +name+ ".java";
        FileWriter writer = new FileWriter(FileUtil.touch(filePath));
        writer.write(StrUtil.format(ClassTemplate,data));
    }
    public static String javaPojoTemplate = "package {package};\n" +
            "\n" +
            "import io.swagger.v3.oas.annotations.media.Schema;\n" +
            "import lombok.Data;\n" +
            "\n" +
            "import javax.validation.constraints.Size;\n" +
            "\n" +
            "@Data\n" +
            "public class {name} {\n" +
            "{arguments}"+"\n"+
            "}";
    public static String ClassTemplate = "{package};\n" +
            "\n" + "{import}" +
            "\n" +
            "{methodModify}"+
            " class {ClassName} {\n" +
            "\n" +
                   "{fields}" +
            "\n" +
                    "{methods}"+
            "}\n";

}
