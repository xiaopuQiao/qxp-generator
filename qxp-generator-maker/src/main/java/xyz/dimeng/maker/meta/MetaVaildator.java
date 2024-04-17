package xyz.dimeng.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import xyz.dimeng.maker.meta.enums.FileGenerateTypeEnum;
import xyz.dimeng.maker.meta.enums.FileTypeEnum;
import xyz.dimeng.maker.meta.enums.ModelTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MetaVaildator {
    public static void doValidAndFill(Meta meta){
        validAndFillMetaRoot(meta);

        validAndFillFileConfig(meta);

        validAndFillModelConfig(meta);

    }

    private static void validAndFillModelConfig(Meta meta) {
        //modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
        if (CollUtil.isEmpty(modelInfoList)) {
            return;
        }
        for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList){
            String groupKey = modelInfo.getGroupKey();
            if(StrUtil.isNotEmpty(groupKey)){
                //生成中间参数 "--author" "--outputText"
                List<Meta.ModelConfig.ModelInfo> subModelInfoList = modelInfo.getModels();
                String allArgStr = subModelInfoList.stream().map(subModelInfo -> {
                    return String.format("\"--%s\"", subModelInfo.getFieldName());
                }).collect(Collectors.joining(", "));
                modelInfo.setAllArgsStr(allArgStr);
                continue;
            }
            String fieldName = modelInfo.getFieldName();
            if(StrUtil.isBlank(fieldName)){
                throw new MetaException("未填写 fieldName");
            }
            String modelInfoType = modelInfo.getType();
            if(StrUtil.isEmpty(modelInfoType)){
                modelInfo.setType(ModelTypeEnum.STRING.getValue());
            }
       }
    }

    private static void validAndFillFileConfig(Meta meta) {
        //fileConfig校验和默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        //sourceRootPath 必填
        String sourceRootPath = fileConfig.getSourceRootPath();
        if(StrUtil.isBlank(sourceRootPath)){
            throw new MetaException("未填写 sourceRootPath");
        }
        //inputRootPath .source + sourceRootPath的最后一个层级路径
        String inputRootPath = fileConfig.getInputRootPath();
        if (StrUtil.isEmpty(inputRootPath)) {
            String defaultInputRootPath = ".source/"  + FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
            fileConfig.setInputRootPath(defaultInputRootPath);
        }
        //outputRootPath 默认为当前路径下的generated
        String outputRootPath = fileConfig.getOutputRootPath();
        String defaultOutputRootPath = "generated";
        if(StrUtil.isEmpty(outputRootPath)){
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }
        String fileConfigType = fileConfig.getType();
        String defaultType = FileTypeEnum.DIR.getValue();
        if(StrUtil.isEmpty(fileConfigType)){
            fileConfig.setType(defaultType);
        }
        List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();
        if (CollUtil.isEmpty(fileInfoList)) {
            return;
        }
        for (Meta.FileConfig.FileInfo fileInfo : fileInfoList){
            String type = fileInfo.getType();
            if(FileTypeEnum.GROUP.getValue().equals(type)){
                continue;
            }
            //inputPath 必填
            String inputPath = fileInfo.getInputPath();
            if(StrUtil.isEmpty(inputPath)){
                throw new MetaException("未填写 inputPath");
            }
            //outputPath 默认等于inputPath
            String outputPath = fileInfo.getOutputPath();
            if(StrUtil.isEmpty(outputPath)){
                fileInfo.setOutputPath(inputPath);
            }
            //type 默认 inputPath 有文件后缀(.java) 默认为file 否则就是dir
            if(StrUtil.isBlank(type)){
                if(StrUtil.isBlank(FileUtil.getSuffix(inputPath))){
                    fileInfo.setType(FileTypeEnum.DIR.getValue());
                }else{
                    fileInfo.setType(FileTypeEnum.FILE.getValue());
                }
            }
            //generateType 文件结尾不为ftl, 值为static,否则值为dynamic
            String generateType = fileInfo.getGenerateType();
            if(StrUtil.isBlank(generateType)){
                if(inputPath.endsWith(".ftl")){
                    fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                }else{
                    fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                }
            }

        }
    }

    private static void validAndFillMetaRoot(Meta meta) {
        //基础信息校验和默认值
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器");
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "dimeng");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "xyz.dimeng");
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());
        meta.setName(name);
        meta.setDescription(description);
        meta.setAuthor(author);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setCreateTime(createTime);
    }
}
