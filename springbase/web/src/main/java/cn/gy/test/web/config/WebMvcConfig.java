package cn.gy.test.web.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import cn.gy.test.configs.constants.ConstantBasic;

@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = { "cn.gy.test.web.controller" })
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 国际化资源文件路径。
     */
    private static final String MESSAGE_SOURCE = "/i18n/messages";

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver url = new InternalResourceViewResolver();
        url.setPrefix("/pages");
        url.setSuffix(".html");
        return url;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver mpartResolver = new CommonsMultipartResolver();
        mpartResolver.setDefaultEncoding(ConstantBasic.CONFIG_ENCODING);
        return mpartResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/images/**").addResourceLocations("/images/");
        registry.addResourceHandler("/i18n/**").addResourceLocations("/i18n/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/");
    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(MESSAGE_SOURCE);
        // 默认为true，设为false才使得国际化有意义
        messageSource.setFallbackToSystemLocale(false);
        // 防止找不到resourcebundle时抛异常
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(5);
        return messageSource;
    }
}
