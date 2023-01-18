package com.day.apidocgennerator.ui;

import cn.hutool.json.JSONObject;
import com.day.apidocgennerator.pojo.MethodVo;
import com.day.apidocgennerator.actions.RightClickMenuAction;
import com.day.apidocgennerator.pojo.Field;
import com.day.apidocgennerator.pojo.Model;
import com.day.apidocgennerator.utils.ChatServer;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


public class ApiDocGenerator extends JFrame implements ActionListener {

    JButton btn_loadController;

    PsiJavaFile CurFile;

    PsiClass CurClass;

    public static Map<String, MethodVo> methodVoList;

    List<String> method_names;

    String METHOD_NOT_FOUND = "无法获得接口,请重新获取";

    JButton startServer;
    JButton stopServer;

    ChatServer webSocketServer;

    Project CurProject;

    List<String> modelDirList = Arrays.asList("pojo","vo");

    public static List<Model> modelList;

    public void init(RightClickMenuAction main) {
        CurFile = main.CurFile;
        CurProject = main.project;
        CurClass = CurFile.getClasses()[0];
        int Method_length = CurClass.getMethods().length;
        if (Method_length == 0) {
            //TODO FaileCHEK
            method_names = new ArrayList<>(1);
            method_names.add(METHOD_NOT_FOUND);
            return;
        }
        method_names = new ArrayList<>(Method_length);
        methodVoList = new HashMap<>(Method_length);
        LoadMethod(CurClass);
        LoadModels();
    }

    public ApiDocGenerator(RightClickMenuAction main) {
        super("ApiDoc Generator");
        init(main);
        setSize(640, 640);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(null);
        btn_loadController = new JButton("加载接口");
        btn_loadController.setLocation(0, 0);
        btn_loadController.setSize(120, 60);
        btn_loadController.addActionListener(this);
        add(btn_loadController);
        startServer = new JButton("启动服务器");
        startServer.setLocation(0,70);
        startServer.setSize(120,60);
        startServer.addActionListener(this);
        add(startServer);
        stopServer = new JButton("关闭服务器");
        stopServer.setLocation(150,70);
        stopServer.setSize(new Dimension(120,60));
        stopServer.addActionListener(this);
        add(stopServer);
        stopServer.revalidate();
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        //重新加载接口
        if (e.getSource().equals(btn_loadController)) {
            LoadMethod(CurClass);
            return;
        }
        //启动和关闭ws服务器
        if(e.getSource().equals(startServer)||e.getSource().equals(stopServer)){
            if(e.getSource().equals(startServer)){
                 webSocketServer = ChatServer.startServer(9984);
                NotificationGroupManager.getInstance()
                .getNotificationGroup("WsServerNotification")
                .createNotification("服务器启动成功,地址ws://localhost:"+webSocketServer.port, NotificationType.INFORMATION)
                .notify(CurProject);
            }else {
                ChatServer.stopServer(webSocketServer);
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("WsServerNotification")
                        .createNotification("服务器关闭成功", NotificationType.INFORMATION)
                        .notify(CurProject);
            }
        }

    }
    private MethodVo getMethodSource(PsiMethod method) {
        String modifier = method.getModifierList().getText();
        String[] returnType = method.getReturnType().getCanonicalText().split("[.]");
        String methodName = method.getName();
        method_names.add(methodName);
        PsiParameter[] parameters = method.getParameterList().getParameters();
        List<JSONObject> method_parmeterList = new ArrayList<>(parameters.length);
        for (PsiParameter parameter : parameters) {
            JSONObject parameterList = new JSONObject();
            PsiAnnotation[] parameterAnnotations = parameter.getAnnotations();
            List<String> annotationNames = new ArrayList<>(parameterAnnotations.length);
            for (PsiAnnotation annotation:parameterAnnotations){
                annotationNames.add(annotation.getQualifiedName());
            }
            parameterList.set("parameterName",parameter.getName());
            parameterList.set("AnnotationList",annotationNames);
            String[] type = parameter.getType().getCanonicalText().split("[.]");
            parameterList.set("Type",type[type.length-1]);
            method_parmeterList.add(parameterList);
        }
        return new MethodVo(methodName, modifier,method_parmeterList,returnType[returnType.length - 1]);
    }
    public void LoadMethod(PsiClass cz) {
        PsiMethod[] methods = cz.getMethods();
        for (PsiMethod method : methods) {
            methodVoList.put(method.getName(), getMethodSource(method));
        }
        System.out.println("");
    }

    private PsiDirectory getDir(PsiFile file,String dirName){
        if(file==null) {
            return null;
        }
        return file.getContainingDirectory().getParentDirectory().findSubdirectory(dirName);
    }

    public void LoadModels(){
        if(CurFile==null) return;
        modelList = new ArrayList<>();
        for (String dirName:modelDirList){
            PsiDirectory dir = getDir(CurFile, dirName);
            if(dir==null) continue;
            PsiFile[] ModelsFile = dir.getFiles();
            for (PsiFile mf:ModelsFile){
                String modelName = mf.getName().replace(".java","");
                PsiField[] nativeFields = ((PsiJavaFile)mf).getClasses()[0].getAllFields();
                List<Field> fieldList = new ArrayList<>(nativeFields.length);
                for (PsiField field:nativeFields){
                    fieldList.add(new Field(field.getName(),field.getType().getPresentableText()));
                }
                modelList.add(new Model(modelName,fieldList));
            }
        }
    }



}
