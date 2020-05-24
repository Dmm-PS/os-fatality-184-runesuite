package io.ruin.services.discord;

import io.ruin.api.utils.ServerWrapper;
import io.ruin.model.World;
import io.ruin.utility.OfflineMode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class DiscordConnection implements EventListener {

	private static JDA jda;
	private static DiscordConnection instance = new DiscordConnection();
	private static long myId;

	public static final long CHANNEL_PUNISHMENTS = 642470243998105652L;

	public static void setup(String token) {
		try {
			jda = new JDABuilder(token).addEventListeners(instance).build();
		} catch (LoginException e) {
            ServerWrapper.logError("Failed to setup discord connection", e);
        }
	}


	@Override
	public void onEvent(@Nonnull GenericEvent genericEvent) {
		if (genericEvent instanceof ReadyEvent) {
			ServerWrapper.println("Discord ready.");

			myId = jda.getSelfUser().getIdLong();
			jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("Playing " + World.type.getWorldName()));

		} else if (genericEvent instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent pm = (PrivateMessageReceivedEvent) genericEvent;

			if (!isMe(pm.getAuthor())) {
				pm.getChannel().sendMessage("Hey there! :wave: I currently don't respond to messages (yet). I love you regardless though :blush:").submit();
			}
		}
    }

	public static void post(long channel, String title, String text) {
        if (!World.isLive() || OfflineMode.enabled) {
            return;
        }
		MessageEmbed built = new EmbedBuilder().setTitle(title).setDescription(text).build();
		post(channel, built);
	}

	public static void post(long channel, MessageEmbed built) {
	    if (!World.isLive() || OfflineMode.enabled) {
            return;
        }
		try {
			jda.getTextChannelById(channel).sendMessage(built).submit();
		} catch (Exception e) {
            ServerWrapper.logError("Failed to send discord message in : " + channel, e);
        }
	}

	private static boolean isMe(ISnowflake who) {
		return who != null && who.getIdLong() == myId;
	}

}
