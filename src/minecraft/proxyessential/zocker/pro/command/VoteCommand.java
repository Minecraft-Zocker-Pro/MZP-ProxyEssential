package minecraft.proxyessential.zocker.pro.command;

import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxyessential.zocker.pro.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class VoteCommand extends Command {

	public VoteCommand() {
		super("vote", "mzp.proxyessential.command.vote", new String[]{"vote", "votes"});
	}

	@Override
	public List<String> onTabCompletion(CommandSender commandSender, String[] strings) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		sender.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.vote.header".replace("%prefix%", Main.ESSENTIAL_MESSAGE.getString("message.prefix")))));

		for (String message : Main.ESSENTIAL_MESSAGE.getStringList("message.command.vote.list")) {
			message = message.replace("&", "§");
			sender.sendMessage(TextComponent.fromLegacyText(message));
		}
	}
}
