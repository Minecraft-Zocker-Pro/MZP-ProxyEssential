package minecraft.proxyessential.zocker.pro.command;

import litebans.api.Database;
import minecraft.proxycore.zocker.pro.OfflineZocker;
import minecraft.proxycore.zocker.pro.Zocker;
import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxycore.zocker.pro.network.NetworkPlayerManager;
import minecraft.proxycore.zocker.pro.storage.StorageManager;
import minecraft.proxycore.zocker.pro.storage.cache.redis.RedisCacheManager;
import minecraft.proxycore.zocker.pro.storage.cache.redis.RedisPacketBuilder;
import minecraft.proxycore.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import minecraft.proxyessential.zocker.pro.Main;
import minecraft.proxyessential.zocker.pro.packet.PlayerMessageChatPacket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageCommand extends Command {

	public static HashMap<String, String> MESSAGE_LAST = new HashMap<>();

	public MessageCommand() {
		super("msg", "mzp.proxyessential.command.message", new String[]{"m", "whisper", "w", "tell", "t"});
	}

	@Override
	public List<String> onTabCompletion(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (args.length <= 1) {
				TextComponent textComponent = new TextComponent();

				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg <player> <message>"));
				textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get an example")));
				textComponent.setText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.message.usage"));

				sender.sendMessage(textComponent);
				return;
			}

			// is player the receiver
			if (sender.getName().equalsIgnoreCase(args[0])) {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.message.self")));
				return;
			}

			UUID receiverUUID = OfflineZocker.fetchUUID(args[0]);

			// is the player in the database available
			if (receiverUUID == null) {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
				return;
			}

			// Is player muted
			if (Main.IS_LITE_BANS_LOADED) {
				if (Database.get().isPlayerMuted((((ProxiedPlayer) sender).getUniqueId()), null)) {
					sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.muted")));
					return;
				}
			}

			// check if player block the receiver
			Zocker zocker = Zocker.getZocker(((ProxiedPlayer) sender).getUniqueId());

			Map<String, String> typeMap = zocker.get("player_proxy_setting_blocked", new String[]{"type"}, new String[]{"player_uuid", "player_uuid_blocked"}, new Object[]{zocker.getUUIDString(), receiverUUID.toString()}).join();

			if (typeMap != null) {
				// receiver blocked the player (sender)
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.you")
					.replace("%receiver%", args[0])));

				return;
			}

			// check if receiver blocked the player (sender)
			OfflineZocker receiverOfflineZocker = new OfflineZocker(receiverUUID);
			Map<String, String> typeMap2 = receiverOfflineZocker.get("player_proxy_setting_blocked", new String[]{"type"}, new String[]{"player_uuid", "player_uuid_blocked"}, new Object[]{receiverOfflineZocker.getUUIDString(), zocker.getUUIDString()}).join();

			if (typeMap2 != null) {
				// receiver blocked the player (sender)
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.receiver")
					.replace("%receiver%", args[0])));

				return;
			}

			NetworkPlayerManager networkPlayerManager = new NetworkPlayerManager();

			// check if the player is online
			boolean isOnline = networkPlayerManager.isOnline(receiverOfflineZocker).join();
			if (isOnline) {
				StringBuilder messageBuilder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					messageBuilder.append(args[i]).append(" ");
				}

				// sender - receiver
				MESSAGE_LAST.put(sender.getName(), args[0]);

				// receiver - sender
				MESSAGE_LAST.put(args[0], sender.getName());

				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.message.sender")
					.replace("%prefix%", Main.ESSENTIAL_MESSAGE.getString("message.prefix"))
					.replace("%receiver%", args[0])
					.replace("%message%", messageBuilder.toString())));

				if (!StorageManager.isRedis()) {
					ProxiedPlayer proxiedReceiverPlayer = ProxyServer.getInstance().getPlayer(receiverUUID);
					if (proxiedReceiverPlayer == null) {
						sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
						return;
					}

					if (!proxiedReceiverPlayer.isConnected()) {
						sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
						return;
					}

					// Spy
					for (UUID spyUUID : SpyCommand.SPIES) {
						ProxiedPlayer spyPlayer = ProxyServer.getInstance().getPlayer(spyUUID);
						if (spyPlayer == null) continue;
						if (!spyPlayer.isConnected()) continue;
						if (spyPlayer.getName().equalsIgnoreCase(sender.getName())) continue;
						if (proxiedReceiverPlayer.getName().equalsIgnoreCase(spyPlayer.getName())) continue;

						spyPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.spy.format")
							.replace("%sender%", sender.getName())
							.replace("%receiver%", proxiedReceiverPlayer.getName())
							.replace("%message%", messageBuilder.toString())));
					}

					proxiedReceiverPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.message.receiver")
						.replace("%prefix%", Main.ESSENTIAL_MESSAGE.getString("message.prefix"))
						.replace("%sender%", sender.getName())
						.replace("%message%", messageBuilder.toString())));
					return;
				}

				RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
				redisPacketBuilder.setPluginName("MZP-ProxyEssential");
				redisPacketBuilder.setSenderName(StorageManager.getServerName());
				redisPacketBuilder.setReceiverName("MZP-ProxyEssential");
				redisPacketBuilder.setServerTargetName("PROXYCORE");

				redisPacketBuilder.addPacket(new PlayerMessageChatPacket(receiverUUID, ((ProxiedPlayer) sender).getUniqueId(), messageBuilder.toString().trim(), RedisPacketIdentifyType.PLAYER_MESSAGE_CHAT));

				new RedisCacheManager().publish(redisPacketBuilder.build());
			} else {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
			}
		}
	}
}
