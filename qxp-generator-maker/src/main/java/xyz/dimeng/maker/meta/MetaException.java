package xyz.dimeng.maker.meta;
/***
* @description 元信息异常
* @return
* @author 乔晓扑
* @date 2024/4/15 22:35
*/
public class MetaException extends RuntimeException{
    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
