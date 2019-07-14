package com.github.kb1000.discordchat.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RequestFuture;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@SideOnly(Side.SERVER)
public class API {
    private static MessageHandler consumer;
    private static JDA jda;
    private static TextChannel textChannel;


    public static boolean message(String playerName, String message) {
        try {
            final RequestFuture<Message> requestFuture = textChannel.sendMessage("**" + playerName + "**: " + message).submit(true);
            boolean error = true;
            while (error) {
                try {
                    requestFuture.get();
                    error = false;
                } catch (InterruptedException e) {
                    error = true; // this helps my bytecode optimizer
                }
            }
            return true;
        } catch (IllegalArgumentException iae) {
            consumer.message("Server", "Nice job " + playerName + "! Your message is longer than I allow!");
            return false;
        } catch (ExecutionException e) {
            throw new RuntimeException("Discord error!", e); // serious error
        }
    }

    public static void start(@NotNull final Config config) {
        final long guild = config.guild;
        final long channel = config.channel;
        final JDABuilder jdaBuilder = new JDABuilder(config.token);
        jdaBuilder.addEventListener(new ListenerAdapter() {
            @Override
            public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
                if (event.getGuild().getIdLong() == guild && event.getChannel().getIdLong() == channel && event.getAuthor().getIdLong() != event.getJDA().getSelfUser().getIdLong()) {
                    consumer.message(event.getMember().getEffectiveName(), event.getMessage().getContentStripped());
                }
            }
        });

        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        textChannel = Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById(guild), "Guild with id " + guild + " not found").getTextChannelById(channel), "Channel with id " + channel + " not found or not a text channel");
    }

    public static void stop() {
        final JDA jda;
        if ((jda = API.jda) != null) {
            jda.shutdown();
            API.jda = null;
        }
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
