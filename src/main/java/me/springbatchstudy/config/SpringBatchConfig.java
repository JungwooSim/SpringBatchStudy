package me.springbatchstudy.config;

import me.springbatchstudy.Listener.ReadListener;
import me.springbatchstudy.model.Library;
import me.springbatchstudy.processor.LibraryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import javax.sql.DataSource;

@Configuration
public class SpringBatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public Job importLibrary() {
        return jobBuilderFactory.get("libraryTest")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Library, Library> chunk(10)
                .reader(reader())
//                .processor(processor())
                .writer(writer())
                .listener(new ReadListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<Library> reader() {
        FlatFileItemReader<Library> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("employee.csv"));
        reader.setStrict(false);

        reader.setLineMapper(new DefaultLineMapper<Library>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] {"col1","col2","col3"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
                setTargetType(Library.class);
            }});
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
        return writer;
//        JdbcBatchItemWriter<LibraryProcessor> writer = new JdbcBatchItemWriter<LibraryProcessor>();
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        writer.setSql("INSERT INTO employee (first_name,last_name,company_name,address,city,county,state,zip) " +
//                "VALUES (:firstName, :lastName,:companyName,:address,:city,:county,:state,:zip)");
//        writer.setDataSource(dataSource);
//        return writer;
    }

}
