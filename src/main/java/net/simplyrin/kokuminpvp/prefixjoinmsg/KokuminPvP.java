package net.simplyrin.kokuminpvp.prefixjoinmsg;

import org.json.JSONObject;

import club.sk1er.mods.kokuminpvp.prefixjoinmsg.Multithreading;
import club.sk1er.mods.kokuminpvp.prefixjoinmsg.Sk1erMod;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = KokuminPvP.MODID, version = KokuminPvP.VERSION)
public class KokuminPvP {

	public static final String MODID = "KokuminPvP-JoinMsg";
	public static final String VERSION = "1.0";

	private boolean isKokumin;
	private boolean isInfo = false;
	private JSONObject object = null;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		Multithreading.runAsync(() -> {
			try {
				JSONObject result = new JSONObject(Sk1erMod.rawWithAgent("https://api.simplyrin.net/Forge-Mods/KokuminPvP/JoinMsg/Players/" + Minecraft.getMinecraft().getSession().getProfile().getId().toString() + ".json"));
				if(result.has("result")) {
					if(result.getBoolean("result")) {
						this.isInfo = true;
					}
				}
			} catch (Exception e) {
			}

			this.object = new JSONObject(Sk1erMod.rawWithAgent("https://api.simplyrin.net/Minecraft-Server/Kokumin/prefixes.json"));
		});
	}

	@SubscribeEvent
	public void onLogin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		String address = event.manager.getRemoteAddress().toString().toLowerCase();

		this.isKokumin = address.contains("kokum.info.tm");
	}

	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) {
		String[] args = ChatColor.stripColor(event.message.getFormattedText()).split(" ");

		if(!this.isKokumin) {
			return;
		}

		if(this.isInfo) {
			return;
		}

		try {
			if(args == null) {
				return;
			}

			if(args[0].isEmpty()) {
				return;
			}

			if(args[1].isEmpty()) {
				return;
			}

			if(args[2].isEmpty()) {
				return;
			}

			if(args[3].isEmpty()) {
				return;
			}
		} catch (Exception e) {}

		if(args[1].equals("joined") && args[2].equals("the") && args[3].equals("game.")) {
			String name = args[0];

			if(object.has(name)) {
				event.setCanceled(true);

				String msg = object.getString("Join-Message");
				msg = msg.replace("%player", ChatColor.translateAlternateColorCodes('&', object.getString(name)) + name);

				KokuminPvP.sendMessage(msg);
			}
		}
	}

	public static void sendMessage(String message) {
		message = message.replaceAll("&", "\u00a7");
		message = message.replaceAll("§", "\u00a7");

		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
	}

}
