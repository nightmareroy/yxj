/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import cn.qeng.gm.core.auth.AuthInterceptor;
import cn.qeng.gm.core.log.LogInterceptor;
import cn.qeng.gm.core.token.TokenInterceptor;

/**
 * spring配置.
 *
 * @author 小流氓(176543888@qq.com)
 */
@EnableWebMvc
@Configuration
@EnableScheduling
@ComponentScan("cn.qeng.gm")
@Import({ SpringJpaConfig.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
class SpringWebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
		registry.addResourceHandler("/mlog/**").addResourceLocations("/mlog/");
	}

	@Bean
	public UrlBasedViewResolver viewResolver() {
		UrlBasedViewResolver resolver = new UrlBasedViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		return resolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		registry.addInterceptor(authInterceptor());
		registry.addInterceptor(tokenInterceptor());
		registry.addInterceptor(logInterceptor());
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	public TokenInterceptor tokenInterceptor() {
		return new TokenInterceptor();
	}

	@Bean
	public AuthInterceptor authInterceptor() {
		return new AuthInterceptor();
	}

	@Bean
	public LogInterceptor logInterceptor() {
		return new LogInterceptor();
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		return new LocaleChangeInterceptor();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("message", "template-log", "template-item", "template-common", "template-func");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean(name = "localeResolver")
	public SessionLocaleResolver localeResolver() {
		return new SessionLocaleResolver();
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver commonsMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		resolver.setMaxUploadSize(1 * 1024 * 1024);
		return new CommonsMultipartResolver();
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(30 * 1000L); // tomcat默认10秒
		configurer.setTaskExecutor(mvcTaskExecutor());// 所借助的TaskExecutor
	}

	@Bean
	public ThreadPoolTaskExecutor mvcTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("mvc-task");
		executor.setCorePoolSize(10);
		executor.setQueueCapacity(100);
		executor.setMaxPoolSize(25);
		return executor;
	}
}