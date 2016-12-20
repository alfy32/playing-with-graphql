package com.alfy.graphql;

import java.time.Clock;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

//  @Bean
//  public RequestContextListener requestContextListener() {
//    return new RequestContextListener();
//  }

//  @Bean
//  public RestTemplate restTemplate() {
//    RestTemplate restTemplate = new RestTemplate();
//    return restTemplate;
//  }

  @Bean
  public RestTemplate restTemplate(List<ClientHttpRequestInterceptor> interceptors) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(interceptors);
    return restTemplate;
  }

  @Bean
  public ClientHttpRequestInterceptor loggingInterceptor() {
    return (httpRequest, bytes, clientHttpRequestExecution) -> {
      long start = Clock.systemUTC().millis();

      ClientHttpResponse clientHttpResponse = clientHttpRequestExecution.execute(httpRequest, bytes);

      long end = Clock.systemUTC().millis();

      LOGGER.info("Request Logger:" +
          " duration=" + (end - start) +
          " request.method=" + httpRequest.getMethod() +
          " request.uri=" + httpRequest.getURI() +
          " response.code=" + clientHttpResponse.getStatusCode() +
          " request.headers=" + httpRequest.getHeaders() +
          " response.headers=" + clientHttpResponse.getHeaders()
      );
      return clientHttpResponse;
    };
  }

  @Bean
  public HandlerInterceptor timingInterceptor() {
    return new HandlerInterceptor() {


      @Override
      public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        httpServletRequest.setAttribute("startTime", Clock.systemUTC().millis());
        return true;
      }

      @Override
      public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        httpServletRequest.setAttribute("endTime", Clock.systemUTC().millis());
      }

      @Override
      public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        long start = (long) httpServletRequest.getAttribute("startTime");
        long end = (long) httpServletRequest.getAttribute("endTime");

        LOGGER.info("Resource Logger:" +
            " duration=" + (end - start) +
            " method=" + httpServletRequest.getMethod() +
            " uri=" + httpServletRequest.getRequestURI()
        );
      }
    };
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(timingInterceptor());
  }
}
