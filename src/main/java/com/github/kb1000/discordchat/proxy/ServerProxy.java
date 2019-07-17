package com.github.kb1000.discordchat.proxy;

import com.github.kb1000.discordchat.discord.API;
import com.github.kb1000.discordchat.discord.MessageHandler;
import com.github.kb1000.discordchat.handler.ServerEventHandler;
import com.google.gson.Gson;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    private static final Logger logger = Logger.getLogger("discordchat.ServerProxy");
    @Override
    public void start() {
        API.setMessageHandler(new MessageHandler() {
            @Override
            public void message(String username, String message) {
                try {
                    MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).sendChatMsg("Discord => " + username + ": " + message);
                } catch (NullPointerException e) {
                    logger.log(Level.WARNING, "Received message before server was ready: " + username + ": " + message, e);
                }
            }
        });
        final String json;
        try {
            // FIXME(kb1000): use standard forge config mechanisms
            json = read(new File("config/discordchat.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        API.start(new Gson().fromJson(json, API.Config.class));
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    @Override
    public void stop() {
        API.stop();
    }

    private static String read(File file) throws IOException {
        try (final FileInputStream input = new FileInputStream(file)) {
            int offset = 0;
            int remaining = (int) file.length();
            byte[] result = new byte[remaining];

            while (remaining > 0) {
                int read = input.read(result, offset, remaining);
                if (read >= 0) {
                    remaining -= read;
                    offset += read;
                } else {
                    break;
                }
            }

            return new String(remaining == 0 ? result : Arrays.copyOf(result, offset), StandardCharsets.UTF_8);
        }
    }
}
