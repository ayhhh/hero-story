package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis使用工具类
 */
public final class RedisUtil {
    /**
     * redis连接池
     */
    private static JedisPool _jedisPool = null;

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
    private RedisUtil(){

    }

    /**
     * 初始化函数
     */
    public static void init(){
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
            LOGGER.info("redis连接成功");
        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }

    public static Jedis getJedis(){
        if(_jedisPool == null){
            throw new RuntimeException("_jedisPool尚未初始化");
        }
        Jedis jedis = _jedisPool.getResource();
        jedis.auth("fh188514124");
        return jedis;
    }
}
