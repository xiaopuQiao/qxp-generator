package xyz.dimeng.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;

/**
 * w文件过滤规则枚举
 */
public enum FileFilterRuleEnum {

    CONTAINS("包含", "contains"),
    STARTS_WITH("前缀匹配", "startsWith"),
    ENDS_WITH("后缀匹配", "endsWith"),
    REGEX("正则", "regex"),
    EQUALS("相等", "equals");

    private final String text;

    private final String value;

    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    /***
    * @description 根据value获取枚举
    * @return
    * @author 乔晓扑
    * @date 2024/4/17 19:23
    */
    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for(FileFilterRuleEnum anEnum : FileFilterRuleEnum.values()){
            if(anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }
}
