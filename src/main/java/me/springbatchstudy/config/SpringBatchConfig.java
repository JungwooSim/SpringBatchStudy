package me.springbatchstudy.config;

import lombok.AllArgsConstructor;
import me.springbatchstudy.Listener.JobListener;
import me.springbatchstudy.Listener.ReadListener;
import me.springbatchstudy.Listener.WriterListener;
import me.springbatchstudy.model.LibraryTmp;
import me.springbatchstudy.model.LibraryTmpDto;
import me.springbatchstudy.processor.LibraryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.List;

@AllArgsConstructor
@Configuration
public class SpringBatchConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;
    public final DataSource dataSource;
    public final LibraryProcessor libraryProcessor;

    @Bean
    public Job importLibrary() throws Exception {
        return jobBuilderFactory.get("libraryTest")
                .listener(jobListener())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<LibraryTmpDto, LibraryTmpDto> chunk(10)
                .reader(reader())
//                .processor(libraryProcessor)
                .writer(writer())
                .listener(readListener())
                .listener(writeListener())
                .build();
    }

    private ItemWriter<? super LibraryTmpDto> writer() {
        JpaItemWriter<LibraryTmpDto> libraryTmpDtoJpaItemWriter = new JpaItemWriter<>();

        return libraryTmpDtoJpaItemWriter;
    }

    @Bean
    public JobListener jobListener() {
        return new JobListener();
    }

    @Bean
    public ReadListener readListener() {
        return new ReadListener();
    }

    @Bean
    public WriterListener writeListener() {
        return new WriterListener();
    }

    @Bean
    public FlatFileItemReader<LibraryTmpDto> reader() {
        return new FlatFileItemReaderBuilder<LibraryTmpDto>()
                .resource(new ClassPathResource("library.csv"))
                .linesToSkip(1)
                .name("LibraryCsvReader")
                .delimited()
                .names("libraryNM","libraryType","bigLocal","smallLocal")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<LibraryTmpDto>(){{
                    setTargetType(LibraryTmpDto.class);
                }}).build();
    }

//    @Bean
//    @StepScope
//    public FlatFileItemReader<LibraryDTO> reader() {
//        FlatFileItemReader<LibraryDTO> reader = new FlatFileItemReader<>();
//        reader.setResource(new ClassPathResource("libraryTest.csv"));
//        reader.setLinesToSkip(1);
//
//        reader.setLineMapper(new DefaultLineMapper<LibraryDTO>() {{
//            setLineTokenizer(new DelimitedLineTokenizer() {{
//                setNames(new String[] {"col1","col2","col3"});
//            }});
//            setFieldSetMapper(new LibraryFileRowMapper());
//        }});
//        return reader;
//    }

//    @Bean
//    public JdbcBatchItemWriter<LibraryTmpDto> writer() {
//        JdbcBatchItemWriter<LibraryTmpDto> writer = new JdbcBatchItemWriter<>();
////        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
////        writer.setSql("INSERT INTO library2 (col_1, col_2, col_3) " +
////                "VALUES (:col1, :col2, :col3)");
////        writer.setDataSource(dataSource);
////        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        return writer;
//    }
}
