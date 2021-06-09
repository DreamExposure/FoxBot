package org.dreamexposure.foxbot.conf;

import java.util.Properties;

public enum Settings {
    SQL_HOST, SQL_PORT,
    SQL_USER, SQL_PASS,
    SQL_DB, SQL_PREFIX,
    REDIS_HOST, REDIS_PORT, REDIS_PASS,
    SHARD_INDEX, SHARD_COUNT,
    BOT_TOKEN, BOT_SECRET, BOT_ID,
    DEBUG_WEBHOOK, STATUS_WEBHOOK, ERROR_WEBHOOK,
    USE_WEBHOOKS, USE_REDIS_STORES,
    PROFILE;

    private String val;

    Settings() {
    }

    public static void init(Properties properties) {
        for (Settings s : values()) {
            s.set(properties.getProperty(s.name()));
        }
    }

    public String get() {
        return val;
    }

    public void set(String val) {
        this.val = val;
    }
}
