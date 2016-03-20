package cn.gy.test.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = {"cn.gy.test.web.resources"})
public class WebMvcConfig extends WebMvcConfigurerAdapter{

}
