package minecraft.proxyessential.zocker.pro.listener;

import minecraft.proxycore.zocker.pro.Zocker;
import minecraft.proxycore.zocker.pro.event.ZockerDataInitializeEvent;
import minecraft.proxyessential.zocker.pro.command.SpyCommand;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ZockerDataInitializeListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onZockerDataInitialize(ZockerDataInitializeEvent event) {
		Zocker zocker = event.getZocker();
		if (SpyCommand.SPIES.contains(zocker.getUUID())) return;

		zocker.isValue("player_proxy_essential", "spy").thenAccept(aBoolean -> {
			if (aBoolean) {
				SpyCommand.SPIES.add(zocker.getUUID());
			}
		});
	}
}
