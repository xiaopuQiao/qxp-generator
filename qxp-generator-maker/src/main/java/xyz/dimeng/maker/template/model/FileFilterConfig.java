package xyz.dimeng.maker.template.model;

import lombok.Builder;
import lombok.Data;

/***
* @description 文件过滤配置
* @return
* @author 乔晓扑
* @date 2024/4/17 19:15
*/
@Data
@Builder
public class FileFilterConfig {
    /***
    * @description 过滤范围
    * @author 乔晓扑
    * @date 2024/4/17 19:14
    */
    private String range;
    /***
     * @description 过滤规则
     * @author 乔晓扑
     * @date 2024/4/17 19:14
     */
    private String rule;
    /***
     * @description 过滤值
     * @author 乔晓扑
     * @date 2024/4/17 19:14
     */
    private String value;

}
