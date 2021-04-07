package minecraft.proxyessential.zocker.pro.packet;

import minecraft.proxycore.zocker.pro.storage.cache.redis.RedisPacketAbstract;
import minecraft.proxycore.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class PlayerMessageChatPacket extends RedisPacketAbstract {

	private final String message;
	private final UUID receiverUUID;
	private final UUID senderUUID;
	private final RedisPacketIdentifyType identifyType;

	public PlayerMessageChatPacket(UUID receiverUUID, UUID senderUUID, String message, RedisPacketIdentifyType identifyType) {
		this.receiverUUID = receiverUUID;
		this.senderUUID = senderUUID;
		this.message = message;
		this.identifyType = identifyType;
	}

	@Override
	public String getIdentify() {
		return this.identifyType.name().toUpperCase();
	}

	@Override
	public JSONObject toJSON() {
		try {
			return new JSONObject()
				.put("identify", this.identifyType.name().toUpperCase())
				.put("receiverUUID", this.receiverUUID.toString())
				.put("senderUUID", this.senderUUID.toString())
				.put("message", this.message);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
