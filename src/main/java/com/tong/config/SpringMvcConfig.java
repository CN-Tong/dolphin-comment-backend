package com.tong.config;

import com.tong.interceptor.RefreshTokenInterceptor;
import com.tong.interceptor.UserLoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class SpringMvcConfig extends WebMvcConfigurationSupport {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将指定资源路径映射到指定路径
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开启静态资源映射");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket docket() {
        log.info("生成接口文档");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("海豚点评项目接口文档")
                .version("1.0")
                .description("海豚点评项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tong.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 配置拦截器
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // token刷新拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns(
                        "/user/**",
                        "/blog/**",
                        "/blog-comments/**",
                        "/follow/**",
                        "/voucher-order/**"
                )
                .order(0);
        // 登录拦截器
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns(
                        "/user/**",
                        "/blog/**",
                        "/blog-comments/**",
                        "/follow/**",
                        "/voucher-order/**"
                )
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/blog/hot"
                ).order(1);
    }
}
