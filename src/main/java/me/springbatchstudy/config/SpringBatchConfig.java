package me.springbatchstudy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.springbatchstudy.Listener.JobListener;
import me.springbatchstudy.Listener.ProcessListener;
import me.springbatchstudy.Listener.ReadListener;
import me.springbatchstudy.Listener.WriterListener;
import me.springbatchstudy.Repository.BigLocalRepository;
import me.springbatchstudy.Repository.LibraryTypeRepository;
import me.springbatchstudy.Repository.SmallLocalRepository;
import me.springbatchstudy.model.*;
import me.springbatchstudy.processor.LibraryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
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

    String RedisInBigLocalName = "library-big-local";
    String RedisInSmallLocalName = "library-small-local";
    String RedisInType = "library-type";

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
                .incrementer(new RunIdIncrementer())
                .build();
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

    // TODO : 다른 정보 redis에 넣는작업 필요
    @Bean
    public ItemProcessor<LibraryTmp, Library> LibraryTmpToLibrary() {
        List<BigLocal> bigLocalList = new ArrayList<>();
        List<SmallLocal> smallLocalList = new ArrayList<>();
        List<LibraryType> libraryTypeList = new ArrayList<>();
        return  LibraryTmp -> {
            bigLocalList.add(BigLocal.builder().bigLocal(LibraryTmp.getBigLocal()).build());
            smallLocalList.add(SmallLocal.builder().smallLocal(LibraryTmp.getSmallLocal()).build());
            libraryTypeList.add(LibraryType.builder().libarayType(LibraryTmp.getLibraryType()).build());

            return Library.builder()
                    .bigLocal(bigLocalList)
                    .smallLocal(smallLocalList)
                    .libraryType(libraryTypeList)
                    .build();
        };
    }


    // TODO : 1. for 문 안에 있는 중복 코드 함수로 변경
    // TODO : 2. library_type 추가
    // TODO : 3. redis key 삭제 필요
    @Bean
    public ItemWriter<Library> jpaPagingItemWriter() {
        Set<String> redisBigLocalData = getRedisValue(RedisInBigLocalName);
        Set<String> redisSmallLocalData = getRedisValue(RedisInSmallLocalName);
        return Items -> {
            for (Library item : Items) {
                for (BigLocal bigLocalList : item.getBigLocal()) {
                    String bigLocal = bigLocalList.getBigLocal();

                    if (!redisBigLocalData.contains(bigLocal)) {
                        bigLocalRepository.save(bigLocalList);
                        setRedisValue(RedisInBigLocalName, bigLocal);
                        redisBigLocalData.add(bigLocal);
                    }
                }

                for (SmallLocal smallLocalList : item.getSmallLocal()) {
                    String smallLocal = smallLocalList.getSmallLocal();

                    if (!redisSmallLocalData.contains(smallLocal)) {
                        smallLocalRepository.save(smallLocalList);
                        setRedisValue(RedisInSmallLocalName, smallLocal);
                        redisSmallLocalData.add(smallLocal);
                    }
                }
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
