package me.springbatchstudy.worker;

import lombok.RequiredArgsConstructor;
import me.springbatchstudy.model.Repository.BigLocalRepository;
import me.springbatchstudy.model.Repository.LibraryRepository;
import me.springbatchstudy.model.Repository.LibraryTypeRepository;
import me.springbatchstudy.model.Repository.SmallLocalRepository;
import me.springbatchstudy.model.*;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ImportLibraryAndWriterDBWorker {
    private final StepBuilderFactory stepBuilderFactory;
    private final BigLocalRepository bigLocalRepository;
    private final SmallLocalRepository smallLocalRepository;
    private final LibraryTypeRepository libraryTypeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final LibraryRepository libraryRepository;
    private final EntityManagerFactory entityManagerFactory;

    String RedisInBigLocalValue = "library-big-local";
    String RedisInSmallLocalValue = "library-small-local";
    String RedisInLibraryTypeValue = "library-type";

    @Bean
    public Step importLibraryAndWriterDB() {
        return stepBuilderFactory.get("ImportLibraryAndWriterDB")
                .<LibraryTmp, Library>chunk(10)
                .reader(jpaPagingItemReader())
                .processor(LibraryTmpToLibrary())
                .writer(jpaPagingItemWriter())
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

    public JpaPagingItemReader<LibraryTmp> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<LibraryTmp>()
                .name("LibraryReadFromLibraryTmpTable")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("SELECT p FROM LibraryTmp p")
                .build();
    }

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

    public ItemWriter<Library> jpaPagingItemWriter() {
        Set<String> redisBigLocalData = getRedisValue(RedisInBigLocalValue);
        Set<String> redisSmallLocalData = getRedisValue(RedisInSmallLocalValue);
        Set<String> redisTypeData = getRedisValue(RedisInLibraryTypeValue);

        return Items -> {
            for (Library item : Items) {
                BigLocal bigLocal = item.getBigLocal();
                String bigLocalValue = bigLocal.getBigLocal();

                if (!redisBigLocalData.contains(bigLocalValue)) {
                    bigLocal = bigLocalRepository.save(bigLocal);
                    setRedisValue(RedisInBigLocalValue, bigLocalValue);
                    redisBigLocalData.add(bigLocalValue);
                } else {
                    bigLocal = bigLocalRepository.findByBigLocal(bigLocalValue);
                }

                SmallLocal smallLocal = item.getSmallLocal();
                String smallLocalValue = smallLocal.getSmallLocal();

                if (!redisSmallLocalData.contains(smallLocalValue)) {
                    smallLocal = smallLocalRepository.save(smallLocal);
                    setRedisValue(RedisInSmallLocalValue, smallLocalValue);
                    redisSmallLocalData.add(smallLocalValue);
                } else {
                    smallLocal = smallLocalRepository.findBySmallLocal(smallLocalValue);
                }

                LibraryType libraryType = item.getLibraryType();
                String libraryTypeValue = libraryType.getLibarayType();

                if (!redisTypeData.contains(libraryTypeValue)) {
                    libraryType = libraryTypeRepository.save(libraryType);
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
