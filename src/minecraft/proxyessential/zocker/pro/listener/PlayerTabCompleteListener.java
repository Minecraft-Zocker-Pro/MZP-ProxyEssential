package minecraft.proxyessential.zocker.pro.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerTabCompleteListener implements Listener {

	@EventHandler
	public void onPlayerTabComplete(TabCompleteEvent e) {
		if (e.getCursor() == null) return;
		if (e.getCursor().startsWith("/")) return;

		String uncompletedPlayer = e.getCursor().toLowerCase();

		if (uncompletedPlayer.lastIndexOf(' ') >= 0) {
			uncompletedPlayer = uncompletedPlayer.substring(uncompletedPlayer.lastIndexOf(' ') + 1);
		}
		for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			if (p.getName().toLowerCase().startsWith(uncompletedPlayer)) {
				e.getSuggestions().add(p.getName());
			}
		}
	}
}