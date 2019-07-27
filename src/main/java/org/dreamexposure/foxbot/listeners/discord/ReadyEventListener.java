package org.dreamexposure.foxbot.listeners.discord;

import org.dreamexposure.foxbot.service.TimerManager;

import discord4j.core.event.domain.lifecycle.ReadyEvent;

public class ReadyEventListener {
    public static void handle(ReadyEvent event) {
        TimerManager.getManager().init();
    }
}
