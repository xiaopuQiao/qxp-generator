package xyz.dimeng.maker.generator.main;

import freemarker.template.TemplateException;

import java.io.IOException;

public class MainGenerator extends GenerateTemplate{
    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputFilePath) {
        System.out.println("不会输出dist了");
    }





}
