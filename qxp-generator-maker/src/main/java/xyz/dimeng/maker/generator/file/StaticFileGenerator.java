package xyz.dimeng.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/***
* @description 静态文件生成器
* @return
* @author 乔晓扑
* @date 2024/1/15 15:06
*/
public class StaticFileGenerator {

    /***
    * @description 复制文件
    * @param inputPath 输入路径
     * @param outputPath 输出路径
    * @return void
    * @author 乔晓扑
    * @date 2024/1/15 15:09
    */
    public static void copyFilesByHutool(String inputPath,String  outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }

}

