package com.tong.utils;

import com.tong.constant.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {

    /**
     * 2023.1.1 00:00:00对应的时间戳
     */
    private static final Long BEGIN_TIMESTAMP = 1672531200L;
    /**
     * 序列号位数
     */
    private static final int COUNT_BITS = 32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Long nextId(String keyPrefix){
        // 1.生成时间戳
        LocalDateTime now = LocalDateTime.now();
        Long timestamp = now.toEpochSecond(ZoneOffset.UTC);
        timestamp = timestamp - BEGIN_TIMESTAMP;
        // 2.生成序列号
        // 2.1获取当前日期，精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyy:MM:dd"));
        // 2.2自增长
        Long count = stringRedisTemplate.opsForValue()
                .increment(RedisConstants.INCREMENT_KEY + keyPrefix + ":" + date);
        // 3.拼接并返回
        return timestamp << COUNT_BITS | count;
    }
}
