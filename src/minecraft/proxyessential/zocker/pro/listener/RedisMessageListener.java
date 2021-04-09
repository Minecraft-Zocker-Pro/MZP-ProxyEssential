package minecraft.proxyessential.zocker.pro.listener;

import minecraft.proxycore.zocker.pro.OfflineZocker;
import minecraft.proxycore.zocker.pro.event.RedisMessageEvent;
import minecraft.proxyessential.zocker.pro.Main;
import minecraft.proxyessential.zocker.pro.command.SpyCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.util.UUID;

public class RedisMessageListener implements Listener {

	@EventHandler
	public void onRedisMessage(RedisMessageEvent event) {
		if (!event.getReceiverName().equalsIgnoreCase("MZP-ProxyEssential")) return;

		try {
			JSONObject packet = event.getPacket();
			if (packet.isNull("identify")) return;

			String identify = event.getPacket().getString("identify");
			if (identify.length() <= 0) return;

			if (identify.equalsIgnoreCase("PLAYER_MESSAGE_CHAT")) {
				String receiverUUID = packet.getString("receiverUUID");
				if (receiverUUID.length() <= 0) return;

				String senderUUID = packet.getString("senderUUID");
				if (senderUUID.length() <= 0) return;

				ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(receiverUUID));
				if (proxiedPlayer == null) return;
				if (!proxiedPlayer.isConnected()) return;

				String message = packet.getString("message");
				if (message.length() <= 0) return;

				String senderName = OfflineZocker.getName(UUID.fromString(senderUUID));
				if (senderName == null) {
					senderName = "ERROR";
				}

				for (UUID spyUUID : SpyCommand.SPIES) {
					ProxiedPlayer spyPlayer = ProxyServer.getInstance().getPlayer(spyUUID);
					if (spyPlayer == null) continue;
					if (!spyPlayer.isConnected()) continue;
					if (spyPlayer.getName().equalsIgnoreCase(senderName)) continue;
					if (proxiedPlayer.getName().equalsIgnoreCase(spyPlayer.getName())) continue;

					spyPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.spy.format")
						.replace("%sender%", senderName)
						.replace("%receiver%", proxiedPlayer.getName())
						.replace("%message%", message)));
				}

				proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.message.receiver")
					.replace("%prefix%", Main.ESSENTIAL_MESSAGE.getString("message.prefix"))
					.replace("%sender%", senderName)
					.replace("%message%", message)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
