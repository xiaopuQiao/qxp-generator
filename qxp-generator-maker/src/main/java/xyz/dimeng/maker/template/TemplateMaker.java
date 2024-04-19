package xyz.dimeng.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import xyz.dimeng.maker.meta.Meta;
import xyz.dimeng.maker.meta.enums.FileGenerateTypeEnum;
import xyz.dimeng.maker.meta.enums.FileTypeEnum;
import xyz.dimeng.maker.template.enums.FileFilterRangeEnum;
import xyz.dimeng.maker.template.enums.FileFilterRuleEnum;
import xyz.dimeng.maker.template.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/***
* @description 模板制作工具
* @author 乔晓扑
* @date 2024/4/17 9:56
*/
public class TemplateMaker {

    /***
    * @description 制作模板
    * @param templateMakerConfig
    * @return long
    * @author 乔晓扑
    * @date 2024/4/19 17:07
    */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Long id = templateMakerConfig.getId();
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig templateMakerOutputConfig = templateMakerConfig.getOutputConfig();
        return makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,templateMakerOutputConfig,id);
    }

    /***
    * @description 制作模板
    * @param meta
     * @param originProjectPath
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param id
    * @return long
    * @author 乔晓扑
    * @date 2024/4/17 13:18
    */

    public static long makeTemplate(Meta meta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, TemplateMakerOutputConfig templateMakerOutputConfig,Long id){
        //没有id则生成
        if(id==null){
            id = IdUtil.getSnowflakeNextId();
        }
        String projectPath = System.getProperty("user.dir");

        //复制目录
        String tempDirPath = projectPath+File.separator+".temp";
        String templatePath = tempDirPath+File.separator+id;
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath,templatePath,true);
        }
        //1.2输入文件信息

        //要挖坑的项目根目录
        String sourceRootPath  = FileUtil.loopFiles(new File(templatePath),1,null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();


        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        //处理模型信息
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);


        //制作文件模板

        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);


        //3生成配置文件 meta.json放在外层
        String metaOutputPath = templatePath+File.separator+"meta.json";

        //已有meta文件,不是第一次制作,则在meta基础上进行修改
        if(FileUtil.exist(metaOutputPath)){
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);
            BeanUtil.copyProperties(meta, oldMeta, CopyOptions.create().ignoreNullValue());
            meta = oldMeta;
            //1.追加配置
            List<Meta.FileConfig.FileInfo> fileInfoList = meta.getFileConfig().getFiles();

            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = meta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            //配置去重
            meta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            meta.getModelConfig().setModels(distinctModels(modelInfoList));
        }else {

            Meta.FileConfig fileConfig = new Meta.FileConfig();

            meta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);

            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);


            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            meta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);

        }

        //额外的输出配置
        if(templateMakerOutputConfig!=null){
            if(templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfig.FileInfo> fileInfoList = meta.getFileConfig().getFiles();
                meta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
        }
        //输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);

        return id;
    }
    /***
    * @description 获取模型配置
    * @param templateMakerModelConfig
    * @return java.util.List<xyz.dimeng.maker.meta.Meta.ModelConfig.ModelInfo>
    * @author 乔晓扑
    * @date 2024/4/19 17:38
    */

    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        if(templateMakerModelConfig==null){
            return newModelInfoList;
        }

        //1.输入信息
        //处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if(CollUtil.isEmpty(models)){
            return newModelInfoList;
        }
        //转换为配置文件接收的modelInfo对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());

        //本次新增的模型列表
        //如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig!=null){
            //复制变量
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);

            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        }else {
            //不分组,添加所有的模型信息列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /***
    * @description 生成多个文件
    * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
    * @return java.util.List<xyz.dimeng.maker.meta.Meta.FileConfig.FileInfo>
    * @author 乔晓扑
    * @date 2024/4/19 17:32
    */
    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        if(templateMakerFileConfig==null){
            return newFileInfoList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        if(CollUtil.isEmpty(fileInfoConfigList)){
            return newFileInfoList;
        }
        //遍历输入文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList){
            String fileInputPath = fileInfoConfig.getPath();
            String condition = fileInfoConfig.getCondition();

            String inputFileAbsolutePath = sourceRootPath +File.separator+fileInputPath;

            //传入绝对路径
            //得到过滤后的文件列表
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());

            //不处理已经生成的.ftl模板文件
            fileList = fileList.stream()
                    .filter(file-> !file.getAbsolutePath().endsWith(".ftl")).collect(Collectors.toList());
            for (File file : fileList){
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath,file,fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        //如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if(fileGroupConfig!=null){
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();

            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    /***
    * @description 制作模板文件
     * @param sourceRootPath
     * @param inputFile
     * @param fileInfoConfig
    * @return xyz.dimeng.maker.meta.Meta.FileConfig.FileInfo
    * @author 乔晓扑
    * @date 2024/4/17 13:56
    */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath,File inputFile, TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {

        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");

        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath+"/", "");
        String fileOutputPath = fileInputPath +".ftl";


        //2使用字符串替换,生成模板文件
        String fileOutputAbsolutePath = fileInputAbsolutePath+".ftl";



        String fileContent;

        //如果已有模板文件,表示不是第一次制作,则在原有模板的基础上再挖坑
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if(hasTemplateFile){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        //支持多个模型,对于同一个文件的内容,遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();

        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            //模型配置
            if(modelGroupConfig==null){
                replacement = String.format("${%s}",fieldName );
            }else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}",groupKey,fieldName );
            }
            newFileContent = StrUtil.replace(newFileContent,modelInfoConfig.getReplaceText(),replacement);

        }

        //文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        //是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        //之前不存在模板文件,并且这次替换没有修改文件的内容,才是静态生成
        //和原文件内容一致,没有挖坑,静态生成
        if(!hasTemplateFile){
            if(contentEquals){
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            }else {
                //输出模板文件
                fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
            }
        }else if(!contentEquals){
            //有模板文件,并且增加了新坑了,要更新模板文件
            FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
        }

        return fileInfo;
    }
    /***
    * @description 文件去重
    * @param fileInfoList
    * @return java.util.List<xyz.dimeng.maker.meta.Meta.FileConfig.FileInfo>
    * @author 乔晓扑
    * @date 2024/4/17 12:41
    */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        //1.将所有文件配置(fileInfo)分为有分组的和无分组的

        //先处理有分组的文件
        //以组为单位划分
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotEmpty(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        //2.同组内配置合并
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry: groupKeyFileInfoListMap.entrySet()){
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            //1.1.1.将文件配置按照输入路径分组
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)).values());
            //使用新的group配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();

            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);
        }
        //3.创建新的文件配置列表(结果列表),先将合并后的分组添加到结果列表
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());


        //4.再将无分组的文件配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)).values()));
        return resultList;
    }
    /***
     * @description 模型去重
     * @param modelInfoList
     * @return java.util.List<xyz.dimeng.ma ker.meta.Meta.ModelConfig.ModelInfo>
     * @author 乔晓扑
     * @date 2024/4/17 12:41
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        //1.将所有模型配置(modelInfo)分为有分组的和无分组的

        //先处理有分组的模型
        //以组为单位划分
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotEmpty(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        //同组内配置合并
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry: groupKeyModelInfoListMap.entrySet()){
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            //1.1.1.将模型配置按照输入路径分组
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());
            //使用新的group配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();

            groupKeyMergedModelInfoMap.put(groupKey,newModelInfo);
        }
        //3.创建新的模型配置列表(结果列表),先将合并后的分组添加到结果列表
        ArrayList<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());


        //4.再将无分组的模型配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values()));
        // 4. 将未分组的模型添加到结果列表
//        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
//                .collect(Collectors.toList());
//        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
//                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values()));
        return resultList;
    }
}
