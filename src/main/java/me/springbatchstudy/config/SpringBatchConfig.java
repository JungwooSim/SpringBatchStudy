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
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@AllArgsConstructor
@Configuration
public class SpringBatchConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;
    public final DataSource dataSource;
    public final LibraryProcessor libraryProcessor;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job importLibrary() {
        return jobBuilderFactory.get("libraryTest")
                .listener(jobListener())
                .flow(step1())
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<LibraryTmpDto, LibraryTmp> chunk(10)
                .reader(reader())
                .processor(fileToTmpProcessor())
                .writer(tmpWriter())
                .listener(readListener())
//                .listener(writeListener())
                .build();
    }

    private ItemProcessor<? super LibraryTmpDto, ? extends LibraryTmp> fileToTmpProcessor() {
        return LibraryTmpDto::toEntity;
    }

    private ItemWriter<? super LibraryTmp> tmpWriter() {
        JpaItemWriter<LibraryTmp> libraryTmpJpaItemWriter = new JpaItemWriter<>();
        libraryTmpJpaItemWriter.setEntityManagerFactory(entityManagerFactory);

        return libraryTmpJpaItemWriter;
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
        BeanWrapperFieldSetMapper<LibraryTmpDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(LibraryTmpDto.class);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
        delimitedLineTokenizer.setIncludedFields(0,1,2,3);
        delimitedLineTokenizer.setNames("libraryNM","libraryType","bigLocal","smallLocal");

        DefaultLineMapper<LibraryTmpDto> defaultLineMapper = new DefaultLineMapper<>();

        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        FlatFileItemReader<LibraryTmpDto> libraryTmpDtoFlatFileItemReaderBuilder = new FlatFileItemReaderBuilder<LibraryTmpDto>()
                .name("LibraryCsvReader")
                .resource(new ClassPathResource("library.csv"))
                .linesToSkip(1)
                .lineMapper(defaultLineMapper)
                .fieldSetMapper(beanWrapperFieldSetMapper)
                .build();

        return libraryTmpDtoFlatFileItemReaderBuilder;
    }

    @Bean
    public Job readLibraryTmpAndWriter() {
        return jobBuilderFactory.get("readLibraryTmpAndWriter")
                .flow(readLibraryTmpAndWriterStep1())
                .end()
                .incrementer()
                .build();
    }

    @Bean
    public Step readLibraryTmpAndWriterStep1() {
        return stepBuilderFactory.get("readLibraryTmpAndWriterStep1")
                .chunk(10)
                .reader()
                .processor()
                .writer()
                .build();
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
