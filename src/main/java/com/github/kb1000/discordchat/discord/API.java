package com.github.kb1000.discordchat.discord;

import net.dv8tion.jda.core.JDABuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

@SideOnly(Side.SERVER)
public class API {
    private static MessageHandler consumer;

    public static void message(String playerName, String message) {
        // FIXME
        System.out.println(playerName + ": " + message);
    }

    public static void start(final Config config) {
        final long guild = config.guild;
        final long channel = config.channel;
        final JDABuilder jdaBuilder = new JDABuilder(config.token);
        jdaBuilder.addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                super.onGuildMessageReceived(event);
            }
        });
        try {
            jdaBuilder.build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
        // FIXME
    }

    public static void setMessageHandler(MessageHandler consumer) {
        API.consumer = consumer;
    }

    public static class Config {
        public String token;
        public long guild;
        public long channel;
    }
}
