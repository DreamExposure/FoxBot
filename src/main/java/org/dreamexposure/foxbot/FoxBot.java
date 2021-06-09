package org.dreamexposure.foxbot;

import org.dreamexposure.foxbot.conf.Settings;
import org.dreamexposure.foxbot.listeners.discord.MemberJoinListener;
import org.dreamexposure.foxbot.listeners.discord.ReadyEventListener;
import org.dreamexposure.foxbot.network.database.DatabaseManager;
import org.dreamexposure.foxbot.service.TimerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.data.stored.ChannelBean;
import discord4j.core.object.data.stored.GuildBean;
import discord4j.core.object.data.stored.MessageBean;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.store.api.mapping.MappingStoreService;
import discord4j.store.jdk.JdkStoreService;
import discord4j.store.redis.RedisStoreService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

@SpringBootApplication(exclude = SessionAutoConfiguration.class)
public class FoxBot {
    private static DiscordClient client;

    public static void main(String[] args) throws IOException {
        Properties p = new Properties();
        p.load(new FileReader("settings.properties"));
        Settings.init(p);

        //TODO: Start logger

        //Create client
        client = createClient();
        if (getClient() == null)
            throw new NullPointerException("Client cannot be Null");

        //Register event handlers
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(ReadyEventListener::handle);
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(MemberJoinListener::handle);

        DatabaseManager.getManager().connectToMySQL();
        DatabaseManager.getManager().handleMigrations();

        //Start Spring
        try {
            SpringApplication app = new SpringApplication(FoxBot.class);
            app.setAdditionalProfiles(Settings.PROFILE.get());
            app.run(args);
        } catch (Exception e) {
            //TODO: send exception to logger and/or to discord webhooks
            System.exit(4);
        }

        //Add Shutdown hooks...
        Runtime.getRuntime().addShutdownHook(new Thread(TimerManager.getManager()::shutdown));
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager.getManager()::disconnectFromMySQL));

        client.login().block();
    }

    private static DiscordClient createClient() {
        //Create client...
        DiscordClientBuilder builder = new DiscordClientBuilder(Settings.BOT_TOKEN.get());
        builder.setShardIndex(Integer.valueOf(Settings.SHARD_INDEX.get()));
        builder.setShardCount(Integer.valueOf(Settings.SHARD_COUNT.get()));
        builder.setInitialPresence(Presence.online(Activity.playing("Booting Up!")));
        //Redis info + store service for caching
        if (Settings.USE_REDIS_STORES.get().equalsIgnoreCase("true")) {
            RedisURI uri = RedisURI.Builder
                    .redis(Settings.REDIS_HOST.get(), Integer.parseInt(Settings.REDIS_PORT.get()))
                    .withPassword(Settings.REDIS_PASS.get())
                    .build();

            RedisStoreService rss = new RedisStoreService(RedisClient.create(uri));

            MappingStoreService mapping = MappingStoreService.create()
                    .setMappings(rss, MessageBean.class, ChannelBean.class, GuildBean.class)
                    .setFallback(new JdkStoreService());

            builder.setStoreService(mapping);
        } else {
            builder.setStoreService(new JdkStoreService());
        }

        return builder.build();
    }

    //Public methods
    public static DiscordClient getClient() {
        return client;
    }
}
