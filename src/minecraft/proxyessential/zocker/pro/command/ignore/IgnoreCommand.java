package minecraft.proxyessential.zocker.pro.command.ignore;

import minecraft.proxycore.zocker.pro.OfflineZocker;
import minecraft.proxycore.zocker.pro.Zocker;
import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxyessential.zocker.pro.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand extends Command {

	public IgnoreCommand() {
		super("ignore", "mzp.proxyessential.command.ignore", new String[]{"block"});
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

				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/block <player>"));
				textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get an example")));
				textComponent.setText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.usage"));

				sender.sendMessage(textComponent);
				return;
			}

			// is player the receiver
			if (sender.getName().equalsIgnoreCase(args[0])) {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.self")));
				return;
			}

			UUID receiverUUID = OfflineZocker.fetchUUID(args[0]);

			// is the player in the database available
			if (receiverUUID == null) {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
				return;
			}

			// check block type args not null
//			if (args[1] == null) {
//				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.usage")));
//				return;
//			}

			// check block type
			String blockType = "private";

//			switch (args[1]) {
//				case "global": {
//					blockType = "global";
//					break;
//				}
//				case "private": {
//					blockType = "private";
//					break;
//				}
//				default: {
//					blockType = "global";
//				}
//			}

			// check if player block the receiver
			Zocker zocker = Zocker.getZocker(((ProxiedPlayer) sender).getUniqueId());

			Map<String, String> typeMap = zocker.get("player_proxy_setting_blocked", new String[]{"type"}, new String[]{"player_uuid", "player_uuid_blocked"}, new Object[]{zocker.getUUIDString(), receiverUUID.toString()}).join();

			if (typeMap != null) {
				// Player is already blocked
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.failed")
					.replace("%receiver%", args[0])));
				return;
			}

//			// check if player is already blocked
//			Zocker zocker = Zocker.getZocker(((ProxiedPlayer) sender).getUniqueId());
//
//			zocker.getList(Main.ESSENTIAL_SETTING_BLOCK_TABLE, new String[]{"player_uuid", "player_uuid_blocked"}, "player_uuid", zocker.getUUIDString()).thenAcceptAsync(blockedList -> {
//				for (String blockedUUID : blockedList) {
//					if (blockedUUID.equals(receiverUUID.toString())) {
//						// Player is already blocked
//						sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.failed")
//							.replace("%receiver%", args[0])));
//						return;
//					}
//				}

			// Add player to block list
			zocker.insert(Main.ESSENTIAL_SETTING_BLOCK_TABLE, new String[]{"player_uuid", "player_uuid_blocked", "type"}, new Object[]{zocker.getUUIDString(), receiverUUID.toString(), blockType});
			sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.success")
				.replace("%type%", blockType)
				.replace("%receiver%", args[0])));
//			});
		}
	}
}
