package xyz.dimeng.generator;

import freemarker.template.TemplateException;
import xyz.dimeng.model.MainTemplateConfig;

import java.io.File;
import java.io.IOException;


public class MainGenerator {
    public static void main(String[] args) throws Exception {



//        String projectPath = System.getProperty("user.dir");
//        System.out.println(projectPath);
//        String inputPath = projectPath + File.separator + "qxp-generator-demo-projects" + File.separator + "acm-template";
//        String outputPath = projectPath;
//        copyFilesByHutool(inputPath, outputPath);
//        String dynamicInputPath = projectPath + File.separator + "qxp-generator-basic" + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
//        String dynamicOutputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java";
//        // 创建数据模型
//        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
//        mainTemplateConfig.setAuthor("qxp");
//        mainTemplateConfig.setOutputText("输出结果");
//        mainTemplateConfig.setLoop(true);
//
//        doGenerate(dynamicInputPath, dynamicOutputPath, mainTemplateConfig);
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("yupi");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");
        doGenerate(mainTemplateConfig);
    }
    /**
     * 生成
     *
     * @param model 数据模型
     * @throws IOException
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
        String inputRootPath = "C:\\Users\\Dell\\Desktop\\qxp-generator\\qxp-generator-demo-projects\\acm-template-pro";
        String outputRootPath = "C:\\Users\\Dell\\Desktop\\qxp-generator";
        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath,"src/xyz/dimeng/acm/Maintemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath,"src/xyz/dimeng/acm/Maintemplate.java").getAbsolutePath();
        DynamicGenerator.doGenerate(inputPath,outputPath,model);
        inputPath = new File(inputRootPath,".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath,".gitignore").getAbsolutePath();
        StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
        inputPath = new File(inputRootPath,"README.md").getAbsolutePath();
        outputPath = new File(outputRootPath,"README.md").getAbsolutePath();
        StaticGenerator.copyFilesByRecursive(inputPath,outputPath);

//        String projectPath = System.getProperty("user.dir");
//        // 整个项目的根路径
//        File parentFile = new File(projectPath).getParentFile();
//        // 输入路径
//        String inputPath = new File(projectPath, "qxp-generator-demo-projects/acm-template").getAbsolutePath();
//        String outputPath = projectPath;
//        // 生成静态文件
//        StaticGenerator.copyFilesByRecursive(inputPath, outputPath);
//        // 生成动态文件
//        String inputDynamicFilePath = projectPath + File.separator + "qxp-generator-basic/src/main/resources/templates/MainTemplate.java.ftl";
//        String outputDynamicFilePath = outputPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
//        DynamicGenerator.doGenerate(inputDynamicFilePath, outputDynamicFilePath, model);
    }
}
