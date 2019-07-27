package org.dreamexposure.foxbot.objects.role;

import discord4j.core.object.util.Snowflake;

public class RoleMirror {
    private Snowflake primaryGuildId;
    private Snowflake primaryGuildRole;

    private Snowflake secondaryGuildId;
    private Snowflake secondaryGuildRole;

    //Setters
    public void setPrimaryGuildId(Snowflake primaryGuildId) {
        this.primaryGuildId = primaryGuildId;
    }

    public void setPrimaryGuildRole(Snowflake primaryGuildRole) {
        this.primaryGuildRole = primaryGuildRole;
    }

    public void setSecondaryGuildId(Snowflake secondaryGuildId) {
        this.secondaryGuildId = secondaryGuildId;
    }

    public void setSecondaryGuildRole(Snowflake secondaryGuildRole) {
        this.secondaryGuildRole = secondaryGuildRole;
    }

    //Getters

    public Snowflake getPrimaryGuildId() {
        return primaryGuildId;
    }

    public Snowflake getPrimaryGuildRole() {
        return primaryGuildRole;
    }

    public Snowflake getSecondaryGuildId() {
        return secondaryGuildId;
    }

    public Snowflake getSecondaryGuildRole() {
        return secondaryGuildRole;
    }
}
