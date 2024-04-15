package xyz.dimeng.maker.model;

import lombok.Data;

/***
* @description 动态模板配置
*/
@Data
public class DataModel {
    /**
    * 作者注释
    */
    private String author;
    /**
     * 输出信息
     */
    private String outputText;
    /**
     * 是否生成循环
     */
    private boolean loop;
}
