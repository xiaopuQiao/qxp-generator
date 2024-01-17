package xyz.dimeng.model;

import lombok.Data;

/***
* @description 静态模板配置
* @return
* @author 乔晓扑
* @date 2024/1/16 15:57
*/
@Data
public class MainTemplateConfig {
    //先明确几个动态生成的需求:
    //1.在代码开头增加作者@Author注释(增加代码)
    //2.修改程序输出的信息提示(替换代码)
    //3.将循环读取输入改为单次读取(可选代码)
    private String author;
    private String outputText;
    /***
    * @description 是否循环(开关)
    * @return
    * @author 乔晓扑
    * @date 2024/1/16 15:58
    */
    private boolean loop;
}
