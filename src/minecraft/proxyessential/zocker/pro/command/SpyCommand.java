package minecraft.proxyessential.zocker.pro.command;

import minecraft.proxycore.zocker.pro.Zocker;
import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxyessential.zocker.pro.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpyCommand extends Command {

	public static final ArrayList<UUID> SPIES = new ArrayList<>();

	public SpyCommand() {
		super("spy", "mzp.proxyessential.command.spy", new String[]{"socialspy"});
	}

	@Override
	public List<String> onTabCompletion(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			Zocker zocker = Zocker.getZocker(((ProxiedPlayer) sender).getUniqueId());
			if (zocker != null) {
				boolean found = zocker.hasValue("player_proxy_essential", "spy");
				if (!found) {
					zocker.insert("player_proxy_essential",
						new String[]{"uuid", "spy"},
						new Object[]{zocker.getUUID(), 1});

					sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.spy.enabled")));
					return;
				}

				zocker.isValue("player_proxy_essential", "spy").thenAccept(aBoolean -> {
					if (aBoolean) {
						zocker.set("player_proxy_essential", "spy", 0);
						sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.spy.disabled")));
						SPIES.remove(zocker.getUUID());
					} else {
						zocker.set("player_proxy_essential", "spy", 1);
						sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.spy.enabled")));
						SPIES.add(zocker.getUUID());
					}
				});
			}

			return;
		}

		sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + "§3You are already seeing all messages :)"));
	}
}
