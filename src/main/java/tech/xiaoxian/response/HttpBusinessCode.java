package tech.xiaoxian.response;

import org.springframework.http.HttpStatus;

/**
 * 业务代码，用于定义和排查错误类型
 */
public enum HttpBusinessCode implements UnifiedResponse.UnifiedResponseCode {
    //请将系统中所有需要特殊区分的异常信息填写到该文件中
    // 2xx
    /**
     * 200 正常值
     */
    SUCCESS(HttpStatus.OK.value(), "success"),
    // 4xx
    /**
     * 401 认证失败
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "用户认证失败"),
    /**
     * 403 没有权限
     */
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "权限不足"),
    /**
     * 404 不存在
     */
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "未配置此接口"),
    // 5xx
    /**
     * 500 服务器异常
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "内部错误"),
    ;
    private final int code;
    private final String msg;

    HttpBusinessCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
