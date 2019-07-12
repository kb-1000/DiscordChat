package com.github.kb1000.discordchat.proxy;

import com.github.kb1000.discordchat.discord.API;
import com.github.kb1000.discordchat.discord.MessageHandler;
import com.github.kb1000.discordchat.handler.ServerEventHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public void start() {
        API.setMessageHandler(new MessageHandler() {
            @Override
            public void message(String username, String message) {
                MinecraftServer.getServer().sendChatToPlayer("Discord => " + username + ": " + message);
            }
        });
        final String token;
        try {
            token = read(new File("bot_token.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        API.start(token);
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
