package it.infn.mw.iam.config;

import java.util.List;

import org.mitre.openid.connect.web.ServerConfigInterceptor;
import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(new UserInfoInterceptor());
    registry.addInterceptor(new ServerConfigInterceptor());

  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/css/**")
      .addResourceLocations("/resources/css/");
    
    registry.addResourceHandler("/images/**")
      .addResourceLocations("/resources/images/");
    
    registry.addResourceHandler("/js/**")
      .addResourceLocations("/resources/js/");
  }

  @Override
  public void configureMessageConverters(
    List<HttpMessageConverter<?>> converters) {

    converters.add(new StringHttpMessageConverter());
    converters.add(new MappingJackson2HttpMessageConverter());
  }

  @Override
  public void configureDefaultServletHandling(
    DefaultServletHandlerConfigurer configurer) {

    configurer.enable();

  }

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {

    registry.addViewController("/login").setViewName("login");
    registry.addViewController("/error").setViewName("error");
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {

    registry.viewResolver(beanNameViewResolver());
    registry.viewResolver(jspViewResolver());
    
  }
  
  
  private InternalResourceViewResolver jspViewResolver() {

    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setViewClass(JstlView.class);
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    resolver.setOrder(2);
    return resolver;
  }

  
  private BeanNameViewResolver beanNameViewResolver() {

    BeanNameViewResolver resolver = new BeanNameViewResolver();
    resolver.setOrder(1);

    return resolver;
  }
}
