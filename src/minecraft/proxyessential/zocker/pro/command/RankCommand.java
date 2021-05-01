package minecraft.proxyessential.zocker.pro.command;

import minecraft.proxycore.zocker.pro.OfflineZocker;
import minecraft.proxycore.zocker.pro.command.Command;
import minecraft.proxyessential.zocker.pro.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankCommand extends Command {

	public RankCommand() {
		super("rank", "mzp.proxyessential.command.rank", new String[]{"ranks"});
	}

	@Override
	public List<String> onTabCompletion(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
			LuckPerms luckPerms = LuckPermsProvider.get();

			OfflineZocker offlineZocker;

			if (args.length == 0) {
				offlineZocker = new OfflineZocker(proxiedPlayer.getUniqueId());
			} else {
				String targetName = args[0];
				if (targetName == null) {
					proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", targetName)));
					return;
				}

				UUID offlineZockerUUID = OfflineZocker.fetchUUID(targetName);
				if (offlineZockerUUID == null) {
					proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.prefix") + Main.ESSENTIAL_MESSAGE.getString("message.player.offline").replace("%player%", targetName)));
					return;
				}

				offlineZocker = new OfflineZocker(offlineZockerUUID);
			}

			proxiedPlayer.sendMessage(new TextComponent(" "));

			if (args.length == 0) {
				proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.rank.header").replace("%player%", "Your")));
			} else {
				proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.rank.header").replace("%player%", args[0])));
			}

			proxiedPlayer.sendMessage(new TextComponent(" "));

			User user = luckPerms.getUserManager().loadUser(offlineZocker.getUUID()).join();
			if (user == null) {
				if (args.length == 0) {
					proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.rank.none").replace("%player%", "You")));
				} else {
					proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.rank.none").replace("%player%", args[0])));
				}

				return;
			}

			Set<InheritanceNode> tempGroups = user.getNodes().stream()
				.filter(NodeType.INHERITANCE::matches)
				.map(NodeType.INHERITANCE::cast)
				.filter(Node::hasExpiry)
				.collect(Collectors.toSet());

			for (InheritanceNode tempGroup : tempGroups) {
				if (tempGroup == null) continue;

				String text = Main.ESSENTIAL_MESSAGE.getString("message.command.rank.format").replace("%rank%", tempGroup.getGroupName().substring(0, 1).toUpperCase() + tempGroup.getGroupName().substring(1));

				long[] time = this.getGroupRemainingTimeFormatted(tempGroup);

				long sec = time[0];
				long min = time[1];
				long hrs = time[2];
				long days = time[3];

				if (min > 0) {
					if (min > 1) {
						text = text.replace("%minutes%", min + " mins");
					} else {
						text = text.replace("%minutes%", min + " minute");
					}
					text = text.replace("%seconds%", "");
				} else {
					if (sec > 0) {
						text = text.replace("%seconds%", sec + " seconds");
					} else {
						text = text.replace("%seconds%", "");
					}
				}
				if (hrs > 0) {
					if (hrs > 1) {
						text = text.replace("%hours%", hrs + " hrs");
					} else {
						text = text.replace("%hours%", hrs + " hour");
					}

				} else {
					text = text.replace("%hours%", "");
				}
				if (days > 0) {
					if (days > 1) {
						text = text.replace("%days%", days + " days");
					} else {
						text = text.replace("%days%", days + " day");
					}

				} else {
					text = text.replace("%days%", "");
				}

				proxiedPlayer.sendMessage(new TextComponent(text));
			}

			if (!tempGroups.isEmpty()) return;

			if (args.length == 0) {
				proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.rank.none").replace("%player%", "You")));
			} else {
				proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.ESSENTIAL_MESSAGE.getString("message.command.rank.none").replace("%player%", args[0])));
			}
		}
	}

	public long[] getGroupRemainingTimeFormatted(InheritanceNode groupNode) {
		if (groupNode == null || groupNode.getExpiryDuration() == null) return new long[]{0, 0, 0, 0};

		long[] times = new long[4];

		long duration = groupNode.getExpiryDuration().getSeconds();
		long days = (duration / 3600 / 24);
		duration = duration - (days * 3600 * 24);
		long hours = (duration / 3600);
		duration = duration - (hours * 3600);
		long minutes = (duration / 60);
		long seconds = duration - (minutes * 60);

		times[0] = seconds;
		times[1] = minutes;
		times[2] = hours;
		times[3] = days;

		return times;
	}
}
