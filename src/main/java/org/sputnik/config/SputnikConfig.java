package org.sputnik.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


@Configuration
public class SputnikConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public SputnikProperties sputnikProperties() {
        return new SputnikProperties();
    }

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper, ClientHttpRequestFactory requestFactory) {
        ArrayList<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);
        messageConverters.add(stringConverter);
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonConverter);
        return new RestTemplate(messageConverters);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(SputnikProperties properties) throws Exception {
        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
        rf.setReadTimeout(properties.httpTimeout);
        rf.setConnectTimeout(properties.httpTimeout);
        return rf;
    }

    @Bean
    public TaskScheduler taskScheduler(SputnikProperties properties){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.scheduleThreads);
        scheduler.setThreadNamePrefix("task-scheduler-");
        return scheduler;
    }

    @Bean
    public TaskExecutor taskExecutor(SputnikProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.executeThreads);
        executor.setMaxPoolSize(properties.executeThreads);
        executor.setThreadNamePrefix("task-executor-");
        return executor;
    }

}
