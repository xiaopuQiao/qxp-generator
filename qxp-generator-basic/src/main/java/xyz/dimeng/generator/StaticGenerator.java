package xyz.dimeng.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/***
* @description 静态文件生成器
* @return
* @author 乔晓扑
* @date 2024/1/15 15:06
*/
public class StaticGenerator {
    public static void main(String[] args) {
        String projectPath =  System.getProperty("user.dir");
//        System.out.println(projectPath);
        String inputPath = projectPath+File.separator+"qxp-generator-demo-projects"+ File.separator+"acm-template";
        String outputPath =projectPath;
        copyFilesByHutool(inputPath,outputPath);
    }
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

    /***
    * @description 递归拷贝文件(递归实现,会将输入目录完整拷贝到输出目录下)
    * @return
    * @author 乔晓扑
    * @date 2024/1/16 9:51
    */
    public static void copyFilesByRecursive(String inputPath,String  outputPath){
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            copyFileByRecursive(inputFile,outputFile);
        } catch (Exception e) {
            System.out.println("文件复制失败");
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private static void copyFileByRecursive(File inputFile, File outputFile) {
        //区分是文件还是目录
        if(inputFile.isDirectory()){
            System.out.println(inputFile.getName());
            File destOutputFile = new File(outputFile,inputFile.getName());
            //如果是目录,首先创建目标目录
            if(!destOutputFile.exists()){
                destOutputFile.mkdirs();
            }
            //获取目录下的所有文件和子目录
            File[] files = inputFile.listFiles();
            //无字文件,直接结束
            if(ArrayUtil.isEmpty(files)){
                return;
            }
            for (File file : files){
                copyFileByRecursive(file,destOutputFile);
            }
        }else {
            //是文件,直接复制到目标目录下
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(),destPath, StandardCopyOption.REPLACE_EXISTING);
        }

    }

}

