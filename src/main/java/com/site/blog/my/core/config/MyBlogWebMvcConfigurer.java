package com.site.blog.my.core.config;

import com.site.blog.my.core.interceptor.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 拦截器的配置类
@Configuration
public class MyBlogWebMvcConfigurer implements WebMvcConfigurer {

	@Autowired
	private AdminLoginInterceptor adminLoginInterceptor;

	public void addInterceptors(InterceptorRegistry registry) {
		// 添加一个拦截器，拦截以/admin为前缀的url路径
		// 【拦截】后端管理系统的所有请求路径都以 /admin 开头，所以拦截的路径为 /admin/** ，
		// 【排除】但是登陆页面以及部分静态资源文件也是以 /admin 开头，所以需要将这些路径排除，配置如上。
		registry.addInterceptor(adminLoginInterceptor)
				.addPathPatterns("/admin/**")
				.excludePathPatterns("/admin/login")
		        .excludePathPatterns("/admin/dist/**")
		        .excludePathPatterns("/admin/plugins/**");
	}
    
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Linux
        // registry.addResourceHandler("/upload/**").addResourceLocations("file:/home/project/upload/");
    	
		// Windows
		// URL映射绝对路径
		// 上传图片绝对路径：C:/MyJava/project/spring/My-Blog/src/main/resources/upload
		registry.addResourceHandler("/upload/**").addResourceLocations("file:C:/MyJava/project/spring/My-Blog/src/main/resources/upload/");


    }
	
}