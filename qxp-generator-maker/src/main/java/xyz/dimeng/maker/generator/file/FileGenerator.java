package xyz.dimeng.maker.generator.file;

import freemarker.template.TemplateException;
import xyz.dimeng.maker.generator.file.DynamicFileGenerator;

import java.io.File;
import java.io.IOException;


public class FileGenerator {

    /**
     * 生成
     *
     * @param model 数据模型
     * @throws IOException
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        // 整个项目的根路径
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String inputPath = new File(projectPath, "qxp-generator-demo-projects/acm-template").getAbsolutePath();
        String outputPath = projectPath;
        // 生成静态文件
        StaticFileGenerator.copyFilesByHutool(inputPath, outputPath);
        // 生成动态文件
        String inputDynamicFilePath = projectPath + File.separator + "qxp-generator-basic/src/main/resources/templates/MainTemplate.java.ftl";
        String outputDynamicFilePath = outputPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerate(inputDynamicFilePath, outputDynamicFilePath, model);
    }
}
