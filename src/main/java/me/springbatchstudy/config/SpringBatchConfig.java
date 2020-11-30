package me.springbatchstudy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.springbatchstudy.worker.ImportLibraryAndWriterDBWorker;
import me.springbatchstudy.worker.ImportTmpLibraryWorker;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SpringBatchConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final DataSource dataSource;
    private final ImportLibraryAndWriterDBWorker importLibraryAndWriterDBWorker;
    private final ImportTmpLibraryWorker importTmpLibraryWorker;

    @Bean
    public Job importTmpLibraryJob() {
        return jobBuilderFactory.get("ImportTmpLibrary")
                .incrementer(new RunIdIncrementer())
                .flow(importTmpLibraryWorker.importTmpLibrary())
                .end()
                .build();
    }

    @Bean
    public Job importLibraryAndWriterDBJob() {
        return jobBuilderFactory.get("ImportLibraryAndWriterDB")
                .incrementer(new RunIdIncrementer())
                .start(importLibraryAndWriterDBWorker.importLibraryAndWriterDB())
                .next(importLibraryAndWriterDBWorker.redisDelete())
                .build();
    }
}
