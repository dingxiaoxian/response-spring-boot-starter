package tech.xiaoxian.response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常返回控制器
 */
@Controller
public class UnifiedResponseErrorController {

    /**
     * 全局异常处理函数url
     */
    public static final String ERROR_URL = "/UnifiedResponseError";
    /**
     * 全局异常处理的Key，用于设定在request.setAttribute中
     */
    public static final String ERROR_KEY_NAME = UnifiedResponseErrorController.class.getName();

    /**
     * 异常处理endpoint
     *
     * @param request http请求
     */
    @RequestMapping(ERROR_URL)
    @ResponseBody
    public void error(HttpServletRequest request) {
        Object errorObject = request.getAttribute(ERROR_KEY_NAME);
        if (errorObject instanceof BusinessException) {
            throw (BusinessException) errorObject;
        }
        throw HttpBusinessCode.PAGE_NOT_FOUND.error();
    }
}
