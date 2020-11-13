package me.springbatchstudy.model.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

@RequiredArgsConstructor
public class LibraryData {
    private final StringRedisTemplate stringRedisTemplate;
}
