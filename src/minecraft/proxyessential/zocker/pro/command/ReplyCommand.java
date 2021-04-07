package minecraft.proxyessential.zocker.pro.command;

import litebans.api.Database;
import minecraft.proxycore.zocker.pro.OfflineZocker;
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
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class ReplyCommand extends Command {

	public ReplyCommand() {
		super("reply", "mzp.proxyessential.command.reply", "r");
	}

	@Override
	public List<String> onTabCompletion(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (args.length <= 0) {
				TextComponent textComponent = new TextComponent();
				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r <message>"));
				textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.ESSENTIAL_MESSAGE.getString("message.command.reply.hover"))));
				textComponent.setText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.reply.usage"));
				sender.sendMessage(textComponent);
				return;
			}

			if (!MessageCommand.MESSAGE_LAST.containsKey(sender.getName())) {
				sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.message.nobody")));
				return;
			}

			UUID receiverUUID = OfflineZocker.fetchUUID(MessageCommand.MESSAGE_LAST.get(sender.getName()));

			// is the player in the database available
			if (receiverUUID == null) {
				sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
				return;
			}

			// Is player muted
			if (Main.IS_LITE_BANS_LOADED) {
				if (Database.get().isPlayerMuted((((ProxiedPlayer) sender).getUniqueId()), null)) {
					sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.muted")));
					return;
				}
			}

			OfflineZocker receiverOfflineZocker = new OfflineZocker(receiverUUID);
			String receiverName = OfflineZocker.getName(receiverUUID);

			NetworkPlayerManager networkPlayerManager = new NetworkPlayerManager();

			// check if the player is online
			boolean isOnline = networkPlayerManager.isOnline(receiverOfflineZocker).join();
			if (isOnline) {
				StringBuilder messageBuilder = new StringBuilder();
				for (String arg : args) {
					messageBuilder.append(arg).append(" ");
				}

				sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.command.message.sender")
					.replace("%prefix%", Main.ESSENTIAL_MESSAGE.getString("message.prefix"))
					.replace("%receiver%", receiverName)
					.replace("%message%", messageBuilder.toString())));

				if (!StorageManager.isRedis()) {
					ProxiedPlayer proxiedReceiverPlayer = ProxyServer.getInstance().getPlayer(receiverUUID);
					if (proxiedReceiverPlayer == null) {
						sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
						return;
					}

					if (!proxiedReceiverPlayer.isConnected()) {
						sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
						return;
					}

					// Spy
					for (UUID spyUUID : SpyCommand.SPIES) {
						ProxiedPlayer spyPlayer = ProxyServer.getInstance().getPlayer(spyUUID);
						if (spyPlayer == null) continue;
						if (!spyPlayer.isConnected()) continue;
						if (spyPlayer.getName().equalsIgnoreCase(sender.getName())) continue;
						if (proxiedReceiverPlayer.getName().equalsIgnoreCase(spyPlayer.getName())) continue;

						spyPlayer.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.command.spy.format")
							.replace("%sender%", sender.getName())
							.replace("%receiver%", proxiedReceiverPlayer.getName())
							.replace("%message%", messageBuilder.toString())));
					}

					proxiedReceiverPlayer.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.command.message.receiver")
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
				sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
			}
		}
	}
}
