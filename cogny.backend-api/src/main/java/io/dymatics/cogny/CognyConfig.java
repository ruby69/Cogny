package io.dymatics.cogny;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.threeten.bp.Duration;

import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.KeyFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.dymatics.cogny.api.controller.GzipRequestFilter;
import io.dymatics.cogny.api.resolver.UserRetrieveResolver;
import io.dymatics.cogny.api.service.UserService;

@Configuration
@EnableWebMvc
@EnableAsync
public class CognyConfig implements WebMvcConfigurer {
    @Autowired private UserService userService;

    @Value("classpath:firebase.json") private Resource firebaseResource;
    @Value("classpath:datastore.json") private Resource datastoreResource;
    @Value("${datastore.kind-key}") private String kindKey;

    private DatastoreOptions datastoreOptions;

    @PostConstruct
    public void postConstruct() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
              .setDatabaseUrl("https://ProjectId.firebaseio.com")
              .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            datastoreOptions = DatastoreOptions.newBuilder()
            .setProjectId("ProjectId")
            .setCredentials(GoogleCredentials.fromStream(datastoreResource.getInputStream()))
            .setRetrySettings(retrySettings())
            .build();
            datastoreOptions.getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RetrySettings retrySettings() {
        return RetrySettings.newBuilder()
            .setMaxAttempts(10)
            .setMaxRetryDelay(Duration.ofMillis(30000L))
            .setTotalTimeout(Duration.ofMillis(120000L))
            .setInitialRetryDelay(Duration.ofMillis(250L))
            .setRetryDelayMultiplier(1.0)
            .setInitialRpcTimeout(Duration.ofMillis(120000L))
            .setRpcTimeoutMultiplier(1.0)
            .setMaxRpcTimeout(Duration.ofMillis(120000L))
            .build();
      }

    @Bean
    public DatabaseReference firebaseDatabse() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Bean
    public Datastore datastore() {
        return datastoreOptions.getService();
    }

    @Bean
    public KeyFactory keyFactory() {
        return datastore().newKeyFactory().setKind(kindKey);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserRetrieveResolver(userService));
    }

    @Bean
    public FilterRegistrationBean<Filter> getFilterRegistrationBean() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>(new GzipRequestFilter());
        registrationBean.addUrlPatterns("/sensinglogs");
        return registrationBean;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(1000000);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        return taskExecutor;
    }

    @Bean
    @Qualifier("dsExecutor")
    public TaskExecutor dsExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setQueueCapacity(100000);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        return taskExecutor;
    }
}
