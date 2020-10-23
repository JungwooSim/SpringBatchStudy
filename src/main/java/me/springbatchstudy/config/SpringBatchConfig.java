package me.springbatchstudy.config;

import me.springbatchstudy.LibraryFileRowMapper;
import me.springbatchstudy.Listener.ReadListener;
import me.springbatchstudy.model.Library;
import me.springbatchstudy.model.LibraryDTO;
import me.springbatchstudy.processor.LibraryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.function.Function;

@Configuration
public class SpringBatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public DataSource dataSource;
    @Autowired
    public LibraryProcessor libraryProcessor;

    @Bean
    public Job importLibrary() throws Exception {
        return jobBuilderFactory.get("libraryTest")
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<LibraryDTO, Library> chunk(10)
                .reader(reader())
                .processor(libraryProcessor)
                .writer(writer())
                .listener(readListener())
                .build();
    }

    @Bean
    public ReadListener readListener() {
        return new ReadListener();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<LibraryDTO> reader() {
        FlatFileItemReader<LibraryDTO> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("libraryTest.csv"));
        reader.setLinesToSkip(1);

        reader.setLineMapper(new DefaultLineMapper<LibraryDTO>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] {"col1","col2","col3"});
            }});
            setFieldSetMapper(new LibraryFileRowMapper());
        }});
        return reader;
    }

    @Bean
    public LibraryProcessor processor() {
        System.out.println("=============");
        return new LibraryProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Library> writer() {
        JdbcBatchItemWriter<Library> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO library2 (col_1, col_2, col_3) " +
                "VALUES (:col1, :col2, :col3)");
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

}
