package minecraft.proxyessential.zocker.pro.command;

import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxyessential.zocker.pro.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class DiscordCommand extends Command {

	public DiscordCommand() {
		super("discord", "mzp.proxyessential.command.discord", new String[]{""});
	}

	@Override
	public List<String> onTabCompletion(CommandSender commandSender, String[] strings) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.discord")));
	}
}
