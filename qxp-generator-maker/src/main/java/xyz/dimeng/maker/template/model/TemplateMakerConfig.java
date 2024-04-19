package xyz.dimeng.maker.template.model;

import lombok.Data;
import xyz.dimeng.maker.meta.Meta;

@Data
public class TemplateMakerConfig {
    private Long id;
    private Meta meta = new Meta();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
    TemplateMakerOutputConfig outputConfig  = new TemplateMakerOutputConfig();
}
