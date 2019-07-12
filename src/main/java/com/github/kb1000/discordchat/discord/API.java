package com.github.kb1000.discordchat.discord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class API {
    private static MessageHandler consumer;

    public static void message(String playerName, String message) {
        // FIXME
        System.out.println(playerName + ": " + message);
    }

    public static void start(String token) {
        // FIXME JDA should have an api that starts a thread
    }

    public static void stop() {
        // FIXME
    }

    public static void setMessageHandler(MessageHandler consumer) {
        API.consumer = consumer;
    }
}
