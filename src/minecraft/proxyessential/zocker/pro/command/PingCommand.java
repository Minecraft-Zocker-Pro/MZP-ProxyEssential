package minecraft.proxyessential.zocker.pro.command;

import minecraft.proxycore.zocker.pro.Zocker;
import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxycore.zocker.pro.network.NetworkPlayerManager;
import minecraft.proxyessential.zocker.pro.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class PingCommand extends Command {

	public PingCommand() {
		super("ping", "mzp.proxyessential.command.ping", new String[]{""});
	}

	@Override
	public List<String> onTabCompletion(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (args.length >= 1) {
				ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[0]);
				if (proxiedPlayer == null || !proxiedPlayer.isConnected()) {
					sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", args[0])));
					return;
				}

				Zocker zockerTarget = Zocker.getZocker(args[0]);

				sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ping.other")
					.replace("%player%", args[0])
					.replace("%ping%", String.valueOf(new NetworkPlayerManager().getPing(zockerTarget).join()))));
				return;
			}

			Zocker zocker = Zocker.getZocker(((ProxiedPlayer) sender).getUniqueId());
			sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ping.self").replace("%ping%", String.valueOf(new NetworkPlayerManager().getPing(zocker).join()))));


		} else {
			sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.ping.self").replace("%ping%", "0")));
		}
	}
}
