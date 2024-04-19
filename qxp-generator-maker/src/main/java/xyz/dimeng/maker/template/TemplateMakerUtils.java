package xyz.dimeng.maker.template;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import xyz.dimeng.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/***
* @description 模板制作工具类
* @return
* @author 乔晓扑
* @date 2024/4/19 18:22
*/
@Data
public class TemplateMakerUtils {
    /***
    * @description 是否从未分组的文件中移除组内的同名文件
    * @param fileInfoList
    * @return java.util.List<xyz.dimeng.maker.meta.Meta.FileConfig.FileInfo>
    * @author 乔晓扑
    * @date 2024/4/19 18:23
    */

    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList){

        //先获取到所有分组
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        //获取所有分组内的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());
        //获取所有分组内文件的输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());


        //移除所有集合内的外层文件
        return fileInfoList.stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}
