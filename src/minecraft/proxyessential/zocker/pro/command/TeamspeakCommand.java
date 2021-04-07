package minecraft.proxyessential.zocker.pro.command;

import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxyessential.zocker.pro.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class TeamspeakCommand extends Command {

	public TeamspeakCommand() {
		super("teamspeak", "mzp.proxyessential.command.discord", new String[]{"ts3"});
	}

	@Override
	public List<String> onTabCompletion(CommandSender commandSender, String[] strings) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		sender.sendMessage(new TextComponent(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.command.teamspeak")));

	}
}
