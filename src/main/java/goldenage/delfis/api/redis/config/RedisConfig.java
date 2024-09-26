package goldenage.delfis.api.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import goldenage.delfis.api.redis.model.Session;

@EnableRedisRepositories(basePackages = "goldenage.delfis.api.redis.repository")
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Session> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Session> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new GenericToStringSerializer<>(String.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Session.class));
        return template;
    }
}
