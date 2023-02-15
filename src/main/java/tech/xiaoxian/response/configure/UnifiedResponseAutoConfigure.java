package tech.xiaoxian.response.configure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import tech.xiaoxian.response.AbstractUnifiedResponseAdvice;
import tech.xiaoxian.response.UnifiedResponseErrorController;

/**
 * 统一返回值自动配置类
 */
@Configuration
@EnableConfigurationProperties(UnifiedResponseProperties.class)
public class UnifiedResponseAutoConfigure {
    private final UnifiedResponseProperties properties;

    /**
     * 构造函数
     *
     * @param properties 嵌入配置文件内容
     */
    public UnifiedResponseAutoConfigure(UnifiedResponseProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    AbstractUnifiedResponseAdvice UnifiedResponseAdvice() {
        return new AbstractUnifiedResponseAdvice(properties.getLog().isPrintError()) {
            @Override
            protected Boolean isPrintError() {
                return null;
            }

            @Override
            protected boolean ignoreBodyRewrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
                return false;
            }
        };
    }

    @Bean
    UnifiedResponseErrorController unifiedResponseErrorController() {
        return new UnifiedResponseErrorController();
    }

}
