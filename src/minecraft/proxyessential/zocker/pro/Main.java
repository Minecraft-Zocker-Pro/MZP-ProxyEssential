package minecraft.proxyessential.zocker.pro;

import minecraft.proxycore.zocker.pro.CorePlugin;
import minecraft.proxycore.zocker.pro.config.Config;
import minecraft.proxycore.zocker.pro.storage.StorageManager;
import minecraft.proxyessential.zocker.pro.command.*;
import minecraft.proxyessential.zocker.pro.command.ignore.IgnoreCommand;
import minecraft.proxyessential.zocker.pro.command.ignore.IgnoreUndoCommand;
import minecraft.proxyessential.zocker.pro.listener.PlayerTabCompleteListener;
import minecraft.proxyessential.zocker.pro.listener.RedisMessageListener;
import minecraft.proxyessential.zocker.pro.listener.ZockerDataInitializeListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;

public class Main extends CorePlugin {

	public static boolean IS_LITE_BANS_LOADED;

	public static Config ESSENTIAL_CONFIG;
	public static Config ESSENTIAL_MESSAGE;

	public static String ESSENTIAL_SETTING_BLOCK_TABLE;

	@Override
	public void onEnable() {
		super.setPluginName("MZP-ProxyEssential");
		super.onEnable();

		this.buildConfig();
		this.verifyDatabase();
		this.registerCommand();
		this.registerListener();

		if (ProxyServer.getInstance().getPluginManager().getPlugin("LiteBans") != null) {
			IS_LITE_BANS_LOADED = true;
		}
	}

	public void buildConfig() {
		// Config		
		ESSENTIAL_CONFIG = new Config("essential.yml", getPluginName());

		ESSENTIAL_CONFIG.setVersion("0.0.1", true);

		// Message
		ESSENTIAL_MESSAGE = new Config("message.yml", getPluginName());

		ESSENTIAL_MESSAGE.set("message.prefix", "&6&l[MZP]&3 ", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.player.offline", "&3Player &6%player% &3is offline.", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.player.muted", "&6You &3are muted!", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.player.invalid", "&3Player &6%player% &3not found.", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.ping.self", "&3Your ping &6%ping% ms.", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.message.usage", "&3Use &6/msg <player> &3to send a message to a player", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.message.self", "&3You can't message yourself!", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.message.sender", "%prefix%&3You&6 > &3%receiver%&6 >>&f %message%", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.message.receiver", "%prefix%&3%sender%&6 > &3You&6 >>&f %message%", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.reply.usage", "&3Use &6/r&3 <<message> &7to reply", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.reply.nobody", "&3No one to reply to!", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.reply.hover", "&3Click to get an example", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.spy.enabled", "&3Spy enabled.", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.spy.disabled", "&3Spy disabled.", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.spy.format", "&7[Spy] &3%sender%&7 > &3%receiver%&7 >>&f %message%", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.discord", "&3Our discord server: &6&nhttps://discord.gg/UqnMuTx", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.website", "&3Our website: &6&nwww.minecraftzocker.net", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.teamspeak", "&3Our teamspeak server: &6&nts.minecraftzocker.net", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.rank.header", "&3-----< &6%player% current ranks&3 >-----", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.rank.none", "&6%player% &3dont have any temporary ranks!", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.rank.format", "&6%rank%&3 %days% %hours% %minutes% %seconds% left", "0.0.1");

		ESSENTIAL_MESSAGE.set("message.command.ignore.usage", "&3Use &6/block <player>", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.self", "&3You can't block yourself :(", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.success", "&3You successfully blocked &6%receiver%&3.", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.failed", "&3You already blocked &6%receiver%&3.", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.receiver", "&3The player &6%receiver% &3blocked you.", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.you", "&3You blocked the player &6%receiver%&3.", "0.0.3");

		ESSENTIAL_MESSAGE.set("message.command.ignore.undo.usage", "&3Use &6/unblock <player &3to unblock a player.", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.undo.self", "&3You can't unblock yourself :(", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.undo.success", "&3You successfully unblocked &6%player%&3.", "0.0.3");
		ESSENTIAL_MESSAGE.set("message.command.ignore.undo.failed", "&3Player &6%player% &3not found in your block list.", "0.0.3");


		ESSENTIAL_MESSAGE.set("message.command.vote.header", "&6&lVoting Pages", "0.0.1");
		ESSENTIAL_MESSAGE.set("message.command.vote.list", new String[]{
			"&31. &6&l&nGoo.gl/T3ZyDO",
			"&32. &6&l&nGoo.gl/fM6IDt",
			"&33. &6&l&nhttps://minecraft-server.eu/vote/index/14ABB"
		}, "0.0.1");

		ESSENTIAL_MESSAGE.setVersion("0.0.3", true);
	}

	@Override
	public void registerListener() {
		PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
		pluginManager.registerListener(this, new RedisMessageListener());
		pluginManager.registerListener(this, new ZockerDataInitializeListener());
		pluginManager.registerListener(this, new PlayerTabCompleteListener());
	}

	@Override
	public void reload() {
		ESSENTIAL_MESSAGE.reload();
		ESSENTIAL_CONFIG.reload();
	}

	@Override
	public void registerCommand() {
		PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

		pluginManager.registerCommand(this, new PingCommand());
		pluginManager.registerCommand(this, new MessageCommand());
		pluginManager.registerCommand(this, new ReplyCommand());
		pluginManager.registerCommand(this, new IgnoreCommand());
		pluginManager.registerCommand(this, new IgnoreUndoCommand());

		if (pluginManager.getPlugin("LuckPerms") != null) {
			pluginManager.registerCommand(this, new RankCommand());
		}

		// Community
		pluginManager.registerCommand(this, new DiscordCommand());
		pluginManager.registerCommand(this, new TeamspeakCommand());
		pluginManager.registerCommand(this, new WebsiteCommand());

		pluginManager.registerCommand(this, new SpyCommand());
		pluginManager.registerCommand(this, new VoteCommand());
	}

	public void verifyDatabase() {
		String createTable = "CREATE TABLE IF NOT EXISTS `player_proxy_essential` (uuid VARCHAR(36) NOT NULL UNIQUE, `spy` tinyint(4) DEFAULT FALSE, FOREIGN KEY (uuid) REFERENCES player (uuid) ON DELETE CASCADE);";

		ESSENTIAL_SETTING_BLOCK_TABLE = "player_proxy_setting_blocked";
		String createSettingTable = "CREATE TABLE IF NOT EXISTS `player_proxy_setting_blocked` (player_uuid VARCHAR(36) NOT NULL, player_uuid_blocked VARCHAR(36) NOT NULL, `type` VARCHAR(16) DEFAULT NULL, FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE);";

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Create table failed.";
			StorageManager.getMySQLDatabase().createTable(createTable);
			StorageManager.getMySQLDatabase().createTable(createSettingTable);
			return;
		}

		assert StorageManager.getSQLiteDatabase() != null : "Create table failed.";
		StorageManager.getSQLiteDatabase().createTable(createTable);
		StorageManager.getSQLiteDatabase().createTable(createSettingTable);
	}
}
