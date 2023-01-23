package tech.xiaoxian.response.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 统一返回值http返回ContentType配置为Json的自动配置类
 */
@Configuration
@EnableWebMvc
public class UnifiedResponseContentTypeConfigurer implements WebMvcConfigurer {
    /**
     * 修改Springboot默认返回Content-Type由 text/plain 改成 application/json
     *
     * @param configurer 配置器
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(new MediaType(MediaType.APPLICATION_JSON));
    }
}
