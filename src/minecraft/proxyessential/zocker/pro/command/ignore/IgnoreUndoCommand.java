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

public class IgnoreUndoCommand extends Command {

	public IgnoreUndoCommand() {
		super("unignore", "mzp.proxyessential.command.ignore.undo", new String[]{"unblock"});
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

				textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/unblock <player>"));
				textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get an example")));
				textComponent.setText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.undo.usage"));

				sender.sendMessage(textComponent);
				return;
			}

			// is player the receiver
			if (sender.getName().equalsIgnoreCase(args[0])) {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.undo.self")));
				return;
			}

			UUID receiverUUID = OfflineZocker.fetchUUID(args[0]);

			// check if receiver is valid
			if (receiverUUID == null) {
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.invalid")
					.replace("%player%", args[0])));
				return;
			}

			// check if player block the receiver
			Zocker zocker = Zocker.getZocker(((ProxiedPlayer) sender).getUniqueId());

			Map<String, String> typeMap = zocker.get("player_proxy_setting_blocked", new String[]{"type"}, new String[]{"player_uuid", "player_uuid_blocked"}, new Object[]{zocker.getUUIDString(), receiverUUID.toString()}).join();

			if (typeMap == null) {
				// Player is already blocked
				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.undo.failed")
					.replace("%player%", args[0])));
				return;
			}
			
//			zocker.getList(Main.ESSENTIAL_SETTING_BLOCK_TABLE, new String[]{"player_uuid", "player_uuid_blocked"}, "player_uuid", zocker.getUUIDString()).thenAcceptAsync(blockedList -> {
//				boolean found = false;
//
//				for (String blockedUUID : blockedList) {
//					if (blockedUUID.equals(receiverUUID.toString())) {
//
//						found = true;
//						break;
//					}
//				}
//
//				// Receiver not found
//				if (!found) {
//					sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.undo.failed")
//						.replace("%player%", args[0])));
//
//					return;
//				}

				// Remove player to block list
				zocker.delete(Main.ESSENTIAL_SETTING_BLOCK_TABLE, new String[]{"player_uuid", "player_uuid_blocked"}, new Object[]{zocker.getUUIDString(), receiverUUID.toString()}).thenAccept(aBoolean -> {
					if (aBoolean) {
						sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ignore.undo.success")
							.replace("%player%", args[0])));

						zocker.delete(Main.ESSENTIAL_SETTING_BLOCK_TABLE, new String[]{"player_uuid"}, new Object[]{zocker.getUUIDString()});
					} else {
						System.out.println("Failed to remove " + args[0] + " from " + sender.getName() + " of the block list");
					}
				});
//			});
		}
	}
}
