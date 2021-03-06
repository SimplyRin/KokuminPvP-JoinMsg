package net.simplyrin.kokuminpvp.prefixjoinmsg;

import org.json.JSONObject;

import club.sk1er.mods.kokuminpvp.prefixjoinmsg.Multithreading;
import club.sk1er.mods.kokuminpvp.prefixjoinmsg.Sk1erMod;
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
	public static final String VERSION = "1.2";

	private boolean isKokumin;
	private String disableMessage = null;
	private boolean isInfo = false;
	private boolean isGlobalInfo = false;
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
			} catch (Exception e) {}

			try {
				JSONObject result = new JSONObject(Sk1erMod.rawWithAgent("https://api.simplyrin.net/Forge-Mods/KokuminPvP/JoinMsg/info.json"));
				if(result.has("disable")) {
					if(result.getBoolean("disable")) {
						this.isGlobalInfo = true;
						this.disableMessage = ChatColor.translateAlternateColorCodes('&', result.has("message") ? result.getString("message") : "&c&lThis is temporarily disabled.");
					}
				}
			} catch (Exception e) {}

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

		if(args.length > 3) {
			if(args[1].equals("joined") && args[2].equals("the") && args[3].equals("game.")) {
				String name = args[0];

				if(this.object.has(name)) {

					if(this.isGlobalInfo) {
						System.out.println(KokuminPvP.getPrefix() + this.disableMessage);
						KokuminPvP.sendMessage(KokuminPvP.getPrefix() + this.disableMessage);
						return;
					}

					event.setCanceled(true);

					try {
						String msg = this.object.getString("Join-Message");
						msg = msg.replace("%player", ChatColor.translateAlternateColorCodes('&', this.object.getString(name)) + name);

						KokuminPvP.sendMessage(msg);
					} catch (Exception e) {
						return;
					}
				}
			}
		}
	}

	private static String getPrefix() {
		return "§7[§cKokuminPvP§7] §r";
	}

	public static void sendMessage(String message) {
		message = message.replaceAll("&", "\u00a7");
		message = message.replaceAll("§", "\u00a7");

		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
	}

}
