package me.springbatchstudy.config;

import me.springbatchstudy.Repository.BigLocalRepository;
import me.springbatchstudy.Repository.SmallLocalRepository;
import me.springbatchstudy.model.BigLocal;
import me.springbatchstudy.model.Library;
import me.springbatchstudy.model.SmallLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


class SpringBatchConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private BigLocalRepository bigLocalRepository;

    @MockBean
    private SmallLocalRepository smallLocalRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    String RedisInBigLocalName = "library-big-local";
    String RedisInSmallLocalName = "library-small-local";
    String RedisInType = "library-type";

    @Test
    void jpaPagingItemWriter() {
        // given
        List<Library> library = new ArrayList<>();

        List<BigLocal> bigLocals = new ArrayList<>();
        bigLocals.add(BigLocal.builder().id(1L).bigLocal("서울").build());
        bigLocals.add(BigLocal.builder().id(2L).bigLocal("대구").build());

        List<SmallLocal> smallLocals = new ArrayList<>();
        smallLocals.add(SmallLocal.builder().id(1L).smallLocal("강남").build());
        smallLocals.add(SmallLocal.builder().id(1L).smallLocal("삼성").build());

        library.add(
                Library.builder()
                        .id(1L)
                        .bigLocal(bigLocals)
                        .smallLocal(smallLocals)
                        .build()
        );

//        Set<String> redisBigLocalData = getRedisValue(RedisInBigLocalName);
//        Set<String> redisSmallLocalData = getRedisValue(RedisInSmallLocalName);

        // then
        for (Library item : library) {
            for (BigLocal bigLocalList : item.getBigLocal()) {
                String bigLocal = bigLocalList.getBigLocal();

//                if (!redisBigLocalData.contains(bigLocal)) {
//                    bigLocalRepository.save(bigLocalList);
//                    setRedisValue(RedisInBigLocalName, bigLocal);
//                    redisBigLocalData.add(bigLocal);
//                }
            }

            for (SmallLocal smallLocalList : item.getSmallLocal()) {
                String smallLocal = smallLocalList.getSmallLocal();

//                if (!redisSmallLocalData.contains(smallLocal)) {
//                    smallLocalRepository.save(smallLocalList);
//                    setRedisValue(RedisInSmallLocalName, smallLocal);
//                    redisSmallLocalData.add(smallLocal);
//                }
            }
        }

        // when
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
