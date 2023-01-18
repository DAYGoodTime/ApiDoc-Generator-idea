package com.day.apidocgennerator.actions;

import cn.hutool.json.JSONUtil;
import com.day.apidocgennerator.ui.ApiDocGenerator;
import com.day.apidocgennerator.utils.GenerateCodeService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;


public class RightClickMenuAction extends AnAction {

    public static PsiJavaFile CurFile = null;

    public static PsiClass CurClass;

    public Project project;

    public static String packagePath;

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            project = event.getProject();
            CurFile = (PsiJavaFile) event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        } catch (ClassCastException exception) {
            //TODO exception
        }
        String[] split = CurFile.getPackageName().split("[.]");
        StringBuffer packageName = new StringBuffer();
        packageName.append("src/main/java/");
        for (int i = 0; i < split.length - 1; i++) {
            packageName.append(split[i]).append("/");
        }
        packagePath = project.getBasePath() + "/" + packageName;
        ApiDocGenerator apiDocGenerator = new ApiDocGenerator(this);
        if (CurFile == null) return;
        CurClass = CurFile.getClasses()[0];
    }
}
