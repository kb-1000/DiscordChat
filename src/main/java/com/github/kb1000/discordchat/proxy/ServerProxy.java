package com.github.kb1000.discordchat.proxy;

import com.github.kb1000.discordchat.discord.API;
import com.github.kb1000.discordchat.discord.MessageHandler;
import com.github.kb1000.discordchat.handler.ServerEventHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

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
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    @Override
    public void stop() {
        API.stop();
    }
}
