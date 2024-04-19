package xyz.dimeng.maker.template.model;

import lombok.Data;
import xyz.dimeng.maker.meta.Meta;
/***
* @description 模板制作配置
* @return
* @author 乔晓扑
* @date 2024/4/19 17:07
*/
@Data
public class TemplateMakerConfig {
    private Long id;
    private Meta meta = new Meta();

    private String originProjectPath;

    private  TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
