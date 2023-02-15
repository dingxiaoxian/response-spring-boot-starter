package tech.xiaoxian.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;


/**
 * 抽象的全局异常处理切面
 */
@RestControllerAdvice
public abstract class AbstractUnifiedResponseAdvice implements ResponseBodyAdvice<Object> {
    /**
     * log对象
     */
    protected Logger logger = LoggerFactory.getLogger(AbstractUnifiedResponseAdvice.class);
    /**
     * json转换对象
     */
    @Resource
    protected ObjectMapper objectMapper;

    /**
     * 默认是否打印异常
     */
    boolean defaultIsPrintError;

    /**
     * 默认构造
     */
    public AbstractUnifiedResponseAdvice() {
        this(false);
    }

    /**
     * 带是否打印异常参数构造
     *
     * @param printError 是否打印异常
     */
    public AbstractUnifiedResponseAdvice(boolean printError) {
        defaultIsPrintError = printError;
    }

    /**
     * 用户需要重写的是否打印异常函数
     *
     * @return 是否打印异常
     */
    protected abstract Boolean isPrintError();

    boolean printError() {
        return Optional.ofNullable(isPrintError()).orElse(defaultIsPrintError);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 是否忽略重写此请求的返回值
     *
     * @param body                  返回值
     * @param returnType            返回类型
     * @param selectedContentType   http返回类型
     * @param selectedConverterType 转换类型
     * @param request               http请求
     * @param response              http返回
     * @return 是否重写
     */
    protected abstract boolean ignoreBodyRewrite(Object body,
                                                 MethodParameter returnType,
                                                 MediaType selectedContentType,
                                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                                 ServerHttpRequest request,
                                                 ServerHttpResponse response);

    @SuppressWarnings("NullableProblems")
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (ignoreBodyRewrite(body, returnType, selectedContentType, selectedConverterType, request, response)) {
            return body;
        }
        if (body instanceof String) {
            // 该部分代码用于避免函数返回String时出现的StringHttpMessageConverter类型转换问题
            // 详见 https://blog.csdn.net/weixin_45176654/article/details/109689869
            try {
                return objectMapper.writeValueAsString(new UnifiedResponse(HttpBusinessCode.SUCCESS, body));
            } catch (JsonProcessingException e) {
                // 理论上该部分应当不会运行
                logger.error("系统在序列化返回值时出现错误{}", body, e);
                throw new RuntimeException(e);
            }
        }
        if (body instanceof UnifiedResponse) {
            return body;
        }
        return new UnifiedResponse(HttpBusinessCode.SUCCESS, body);
    }

    // 以下函数均为异常处理函数

    /**
     * 系统自身业务异常处理
     *
     * @param businessException 业务异常
     * @return 统一返回信息
     */
    @ExceptionHandler(value = BusinessException.class)
    public UnifiedResponse handleBusinessException(BusinessException businessException) {
        UnifiedResponse response = businessException.getResponse();
        if (printError()) {
            logger.error("异常码: {} 异常原因: {}", response.getCode(), response.getMessage(), businessException);
        } else {
            logger.error("异常码: {} 异常原因: {}", response.getCode(), response.getMessage());
        }
        return response;
    }

    /**
     * 404异常处理
     *
     * @param exception 404异常
     * @return 404返回值，并提示相应信息
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UnifiedResponse handleException(NoHandlerFoundException exception) {
        if (printError()) {
            logger.error("访问未定义接口: {}", exception.getLocalizedMessage());
            return new UnifiedResponse(HttpBusinessCode.PAGE_NOT_FOUND, exception.getLocalizedMessage());
        } else {
            return new UnifiedResponse(HttpBusinessCode.PAGE_NOT_FOUND, null);
        }
    }

    /**
     * 最后的未知异常处理
     *
     * @param exception 未知异常
     * @return 500返回值，并提示相应信息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UnifiedResponse handleException(Exception exception) {
        logger.error("未知异常: {}", exception.getLocalizedMessage(), exception);
        if (printError()) {
            return new UnifiedResponse(HttpBusinessCode.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
        } else {
            return new UnifiedResponse(HttpBusinessCode.INTERNAL_SERVER_ERROR, null);
        }
    }
}
