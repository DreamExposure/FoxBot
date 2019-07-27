package org.dreamexposure.foxbot.listeners.discord;

import org.dreamexposure.foxbot.FoxBot;
import org.dreamexposure.foxbot.network.database.DatabaseManager;
import org.dreamexposure.foxbot.objects.role.RoleMirror;

import java.util.List;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

public class MemberJoinListener {
    public static void handle(MemberJoinEvent event) {
        List<RoleMirror> mirrors = DatabaseManager.getManager().getRoleMirrorBySecondary(event.getGuildId());

        for (RoleMirror m : mirrors) {
            //Check if user is in primary guild.
            Guild primaryGuild = FoxBot.getClient().getGuildById(m.getPrimaryGuildId()).onErrorResume(e -> Mono.empty()).block();
            if (primaryGuild != null) {
                Member primaryMember = primaryGuild.getMemberById(event.getMember().getId()).onErrorResume(e -> Mono.empty()).block();

                if (primaryMember != null) {
                    if (primaryMember.getRoleIds().contains(m.getPrimaryGuildRole())) {
                        event.getMember().addRole(m.getSecondaryGuildRole(), "Role Mirror: " + primaryGuild.getName()).onErrorResume(e -> Mono.empty()).subscribe();
                    }
                }
            }
        }
    }
}
