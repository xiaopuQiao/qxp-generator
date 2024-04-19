package xyz.dimeng.maker.template.model;

import lombok.Data;

@Data
public class TemplateMakerOutputConfig {
    //是否从未分组的文件中移除组内的同名文件
    private boolean removeGroupFilesFromRoot = true;


}
