package xyz.dimeng.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {
    //volatile用于确保多线程环境下的内存可见性
    private static volatile Meta meta;
    public static Meta getMetaObject(){
        if(meta == null){
            synchronized (MetaManager.class){
                if(meta==null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }
    public static Meta initMeta(){
//        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        // 校验配置文件,处理默认值
        MetaVaildator.doValidAndFill(newMeta);
        return newMeta;
    }

}
