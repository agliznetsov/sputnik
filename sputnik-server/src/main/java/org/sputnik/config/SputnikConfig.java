package org.sputnik.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.sputnik.context.Launcher;
import org.sputnik.dao.DataProfileRepository;
import org.sputnik.dao.DataSourceRepository;

import java.io.File;
import java.util.ArrayList;


@Configuration
public class SputnikConfig {
    public static final String PROFILES_DIR = "config/profiles";
    public static final String SOURCES_DIR = "config/sources";
    public static final String DATA_DIR = "data";

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT");
            }
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public SputnikProperties sputnikProperties() {
        return new SputnikProperties();
    }

    @Bean
    public DataProfileRepository dataProfileRepository(SputnikProperties sputnikProperties) {
        return new DataProfileRepository(new File(sputnikProperties.getHomeDirectory(), PROFILES_DIR));
    }

    @Bean
    public DataSourceRepository dataSourceRepository(SputnikProperties sputnikProperties) {
        return new DataSourceRepository(new File(sputnikProperties.getHomeDirectory(), SOURCES_DIR));
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
        rf.setReadTimeout(properties.timeout);
        rf.setConnectTimeout(properties.timeout);
        return rf;
    }

    @Bean
    public TaskScheduler taskScheduler(SputnikProperties properties){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setDaemon(true);
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("task-scheduler-");
        return scheduler;
    }

    @Bean
    public TaskExecutor taskExecutor(SputnikProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setDaemon(true);
        executor.setCorePoolSize(properties.collectThreads);
        executor.setMaxPoolSize(properties.collectThreads);
        executor.setThreadNamePrefix("task-executor-");
        return executor;
    }

    @Bean
    public TaskExecutor outputReader(SputnikProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setDaemon(true);
        executor.setCorePoolSize(properties.collectThreads);
        executor.setMaxPoolSize(properties.collectThreads);
        executor.setThreadNamePrefix("output-reader-");
        return executor;
    }

    @Bean
    public Launcher launcher() {
        return new Launcher();
    }

}
