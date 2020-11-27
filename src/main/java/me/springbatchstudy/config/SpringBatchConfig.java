package me.springbatchstudy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.springbatchstudy.Listener.JobListener;
import me.springbatchstudy.Listener.ProcessListener;
import me.springbatchstudy.Listener.ReadListener;
import me.springbatchstudy.Listener.WriterListener;
import me.springbatchstudy.Repository.BigLocalRepository;
import me.springbatchstudy.Repository.LibraryRepository;
import me.springbatchstudy.Repository.LibraryTypeRepository;
import me.springbatchstudy.Repository.SmallLocalRepository;
import me.springbatchstudy.model.*;
import me.springbatchstudy.processor.LibraryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SpringBatchConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;
    public final DataSource dataSource;
    public final LibraryProcessor libraryProcessor;
    private final EntityManagerFactory entityManagerFactory;
    private final BigLocalRepository bigLocalRepository;
    private final SmallLocalRepository smallLocalRepository;
    private final LibraryTypeRepository libraryTypeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final LibraryRepository libraryRepository;

    String RedisInBigLocalValue = "library-big-local";
    String RedisInSmallLocalValue = "library-small-local";
    String RedisInLibraryTypeValue = "library-type";

    @Bean
    public Job importLibrary() {
        return jobBuilderFactory.get("libraryTest")
                .listener(jobListener())
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
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
                .start(readLibraryTmpAndWriterStep1())
                .next(redisDelete())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    @Bean
    public Step redisDelete() {
        return stepBuilderFactory.get("redisDelete")
                .tasklet((contribution, chunkContext) -> {
                    redisTemplate.delete(RedisInBigLocalValue);
                    redisTemplate.delete(RedisInSmallLocalValue);
                    redisTemplate.delete(RedisInLibraryTypeValue);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step readLibraryTmpAndWriterStep1() {
        return stepBuilderFactory.get("readLibraryTmpAndWriterStep1")
                .<LibraryTmp, Library>chunk(10)
                .reader(jpaPagingItemReader())
                .processor(LibraryTmpToLibrary())
                .writer(jpaPagingItemWriter())
                .listener(new ProcessListener())
                .build();
    }

    @Bean
    public JpaPagingItemReader<LibraryTmp> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<LibraryTmp>()
                .name("LibraryReadFromLibraryTmpTable")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("SELECT p FROM LibraryTmp p")
                .build();
    }

    @Bean
    public ItemProcessor<LibraryTmp, Library> LibraryTmpToLibrary() {
        return  LibraryTmp -> {
            return Library.builder()
                    .libraryNM(LibraryTmp.libraryNM)
                    .bigLocal(BigLocal.builder().bigLocal(LibraryTmp.getBigLocal()).build())
                    .smallLocal(SmallLocal.builder().smallLocal(LibraryTmp.getSmallLocal()).build())
                    .libraryType(LibraryType.builder().libarayType(LibraryTmp.getLibraryType()).build())
                    .build();
        };
    }

    @Bean
    public ItemWriter<Library> jpaPagingItemWriter() {
        Set<String> redisBigLocalData = getRedisValue(RedisInBigLocalValue);
        Set<String> redisSmallLocalData = getRedisValue(RedisInSmallLocalValue);
        Set<String> redisTypeData = getRedisValue(RedisInLibraryTypeValue);

        return Items -> {
            for (Library item : Items) {
                BigLocal bigLocal = item.getBigLocal();
                String bigLocalValue = bigLocal.getBigLocal();

                if (!redisBigLocalData.contains(bigLocalValue)) {
                    bigLocalRepository.saveAndFlush(bigLocal);
                    setRedisValue(RedisInBigLocalValue, bigLocalValue);
                    redisBigLocalData.add(bigLocalValue);
                } else {
                    bigLocal = bigLocalRepository.findByBigLocal(bigLocalValue);
                }

                SmallLocal smallLocal = item.getSmallLocal();
                String smallLocalValue = smallLocal.getSmallLocal();

                if (!redisSmallLocalData.contains(smallLocalValue)) {
                    smallLocalRepository.saveAndFlush(smallLocal);
                    setRedisValue(RedisInSmallLocalValue, smallLocalValue);
                    redisSmallLocalData.add(smallLocalValue);
                } else {
                    smallLocal = smallLocalRepository.findBySmallLocal(smallLocalValue);
                }

                LibraryType libraryType = item.getLibraryType();
                String libraryTypeValue = libraryType.getLibarayType();

                if (!redisTypeData.contains(libraryTypeValue)) {
                    libraryTypeRepository.saveAndFlush(libraryType);
                    setRedisValue(RedisInLibraryTypeValue, libraryTypeValue);
                    redisTypeData.add(libraryTypeValue);
                } else {
                    libraryType = libraryTypeRepository.findByLibarayType(libraryTypeValue);
                }

                libraryRepository.save(Library.builder()
                        .libraryNM(item.getLibraryNM())
                        .bigLocal(bigLocal)
                        .smallLocal(smallLocal)
                        .libraryType(libraryType)
                        .build());
            }
        };
    }

    private void setRedisValue(String key, String value) {
        SetOperations<String, String> data = redisTemplate.opsForSet();
        data.add(key, value);
    }

    private Set<String> getRedisValue(String key) {
        SetOperations<String, String> data = redisTemplate.opsForSet();
        Set<String> test = data.members(key);

        return test;
    }
}
