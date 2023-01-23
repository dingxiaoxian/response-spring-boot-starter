package tech.xiaoxian.response.configure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        };
    }

    @Bean
    UnifiedResponseErrorController unifiedResponseErrorController() {
        return new UnifiedResponseErrorController();
    }

}
