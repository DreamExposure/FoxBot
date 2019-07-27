package org.dreamexposure.foxbot.modules.misc;


import org.dreamexposure.foxbot.FoxBot;
import org.dreamexposure.foxbot.conf.GlobalVars;
import org.dreamexposure.foxbot.conf.Settings;

import java.util.ArrayList;
import java.util.TimerTask;

import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

public class PresenceUpdater extends TimerTask {
    private final ArrayList<String> statuses = new ArrayList<>();
    private int index;

    /**
     * Creates the StatusChanger and its Statuses list.
     */
    public PresenceUpdater() {
        statuses.add("FoxBot!");
        statuses.add("=help for help");
        statuses.add("$foxbot for info");
        statuses.add("Powered by DreamExposure");
        statuses.add("Used on %guCount% guilds!");
        statuses.add("%shards% shards!");
        statuses.add("Version " + GlobalVars.version);
        statuses.add("FoxBot is on Patreon!");
        index = 0;
    }

    @Override
    public void run() {
        String status = statuses.get(index);
        status = status.replace("%guCount%", FoxBot.getClient().getGuilds().count().block() + "");
        status = status.replace("%shards%", Settings.SHARD_COUNT.get() + "");

        FoxBot.getClient().updatePresence(Presence.online(Activity.playing(status))).subscribe();

        //Set new index.
        if (index + 1 >= statuses.size())
            index = 0;
        else
            index++;
    }
}
