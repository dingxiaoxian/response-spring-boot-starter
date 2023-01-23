package tech.xiaoxian.response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 内部业务异常
 */
public class BusinessException extends RuntimeException {
    /**
     * 业务异常包裹的返回值信息
     */
    private final UnifiedResponse response;

    /**
     * 构造业务异常类
     *
     * @param code 异常码
     * @param msg  异常信息
     * @param data 异常数据内容
     */
    protected BusinessException(int code, String msg, Object data) {
        this.response = new UnifiedResponse(code, msg, data);
    }

    /**
     * 获取异常实际返回值
     *
     * @return 统一返回值类型
     */
    public UnifiedResponse getResponse() {
        return response;
    }

    /**
     * 转发异常到全局异常处理控制器
     *
     * @param request  http请求
     * @param response http返回值
     * @throws ServletException servlet异常
     * @throws IOException      io异常
     */
    public void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(UnifiedResponseErrorController.ERROR_KEY_NAME, this);
        request.getRequestDispatcher(UnifiedResponseErrorController.ERROR_URL).forward(request, response);
    }
}
