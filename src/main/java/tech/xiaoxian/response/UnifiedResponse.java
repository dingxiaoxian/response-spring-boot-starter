package tech.xiaoxian.response;


/**
 * 统一返回值对象
 */
public class UnifiedResponse {
    private final int code;
    private final String msg;
    private final Object data;


    /**
     * 基础返回码接口
     */
    public interface UnifiedResponseCode {
        /**
         * 获取返回码
         *
         * @return 返回码
         */
        int getCode();

        /**
         * 获取返回信息
         *
         * @return 返回信息
         */
        String getMsg();

        /**
         * 返回业务码对应的异常
         *
         * @return 异常
         */
        default BusinessException error() {
            return new BusinessException(this.getCode(), this.getMsg(), null);
        }

        /**
         * 返回业务码对应的异常
         *
         * @param msg  自定义消息信息
         * @param data 自定义数据内容
         * @return 异常
         */
        default BusinessException error(String msg, Object data) {
            return new BusinessException(this.getCode(), msg, data);
        }

    }

    /**
     * 构造函数
     *
     * @param code 返回码
     * @param msg  返回提示信息
     * @param data 返回数据内容
     */
    public UnifiedResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;

    }

    /**
     * 通过返回码构造返回值
     *
     * @param responseCode 返回码信息
     * @param data         返回数据内容
     */
    public UnifiedResponse(UnifiedResponseCode responseCode, Object data) {
        this(responseCode.getCode(), responseCode.getMsg(), data);
    }

    /**
     * 获取返回码
     *
     * @return 返回码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取返回信息
     *
     * @return 返回信息
     */
    public String getMessage() {
        return msg;
    }

    /**
     * 获取返回数据
     *
     * @return 返回数据
     */
    public Object getData() {
        return data;
    }
}
