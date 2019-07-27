package org.dreamexposure.foxbot.objects.guild;

import discord4j.core.object.util.Snowflake;

public class GuildSettings {
    private final Snowflake id;
    private String lang = "ENGLISH";
    private String prefix = "$";
    private boolean patron;
    private boolean dev;


    public GuildSettings(Snowflake id) {
        this.id = id;
    }

    //Setters
    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPatron(boolean patron) {
        this.patron = patron;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    //Getters
    public Snowflake getGuildId() {
        return id;
    }

    public String getLang() {
        return lang;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isPatron() {
        return patron;
    }

    public boolean isDev() {
        return dev;
    }
}
