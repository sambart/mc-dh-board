package com.sambart.dh;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.sambart.dh.commands.DSSCommand;
import com.sambart.dh.events.DSSEvent;
import com.sambart.dh.functions.DSSFunction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class RankBoard extends JavaPlugin {
    private DPPCore core;
    private static RankBoard plugin;
    public String prefix;
    public YamlConfiguration config;
    public DLang lang;
    public Map<String, YamlConfiguration> shops = new HashMap<>();
    public final Map<UUID, String> currentEditShop = new HashMap<>();
    public final Map<UUID, ItemStack> currentItem = new HashMap<>();
    public final Map<UUID, Boolean> isBuying = new HashMap<>();
    public boolean preventInvClose;

    public static RankBoard getInstance() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        Plugin pl = getServer().getPluginManager().getPlugin("DPP-Core");
        if(pl == null) {
            getLogger().warning("DPP-Core 플러그인이 설치되어있지 않습니다.");
            getLogger().warning("DPP-Core plugin is not installed.");
            plugin.setEnabled(false);
            return;
        }
        core = (DPPCore) pl;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        preventInvClose = config.getBoolean("Settings.preventInvClose");
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.prefix"));
        lang = new DLang(config.getString("Settings.Lang") == null ? "Korean" : config.getString("Settings.Lang"), plugin);
        DSSFunction.loadAllShops();
        plugin.getServer().getPluginManager().registerEvents(new DSSEvent(), plugin);
        getCommand("상점").setExecutor(new DSSCommand());

    }
    
    public void onDisable() {
        DSSFunction.saveAllShop();
    }

}
