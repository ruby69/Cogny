package io.dymatics.cogny;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Configuration
@EnableWebMvc
@EnableScheduling
public class CognyConfig implements WebMvcConfigurer {
    @Value("classpath:firebase.json") private Resource firebaseResource;
    @Value("classpath:datastore.json") private Resource datastoreResource;

    private DatastoreOptions datastoreOptions;

    @PostConstruct
    public void postConstruct() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
              .setDatabaseUrl("https://bluetire-182703.firebaseio.com")
              .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            datastoreOptions = DatastoreOptions.newBuilder()
            .setProjectId("bluetire-182703")
            .setCredentials(GoogleCredentials.fromStream(datastoreResource.getInputStream())).build();
            datastoreOptions.getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(1000000);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        return taskExecutor;
    }
}
