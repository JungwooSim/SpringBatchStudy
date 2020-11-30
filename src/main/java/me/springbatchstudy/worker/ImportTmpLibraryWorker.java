package me.springbatchstudy.worker;

import lombok.RequiredArgsConstructor;
import me.springbatchstudy.model.LibraryTmp;
import me.springbatchstudy.model.LibraryTmpDto;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

@RequiredArgsConstructor
@Component
public class ImportTmpLibraryWorker {
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Step importTmpLibrary() {
        return stepBuilderFactory.get("ImportTmpLibrary")
                .<LibraryTmpDto, LibraryTmp> chunk(10)
                .reader(reader())
                .processor(fileToTmpProcessor())
                .writer(tmpWriter())
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
}
