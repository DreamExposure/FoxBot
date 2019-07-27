package org.dreamexposure.foxbot.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SuppressWarnings("deprecation")
@Configuration
@Profile({"prod", "dev"})
@EnableRedisHttpSession
public class LettuceConfig {
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        final LettuceConnectionFactory factory = new LettuceConnectionFactory();

        factory.setHostName(Settings.REDIS_HOST.get());
        factory.setPort(Integer.parseInt(Settings.REDIS_PORT.get()));
        factory.setPassword(Settings.REDIS_PASS.get());

        return factory;
    }
}
