package com.sambart.dh.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.sambart.dh.RankBoard;
import com.sambart.dh.functions.DSSFunction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@SuppressWarnings("all")
public class DSSEvent implements Listener {
    private final RankBoard plugin = RankBoard.getInstance();

    //빌리저 클릭 이벤트
    @EventHandler
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        Player p = (Player) event.getPlayer();
        Villager villager = (Villager) event.getRightClicked();
        String villagerName = villager.getName();
        if (event.getRightClicked() instanceof Villager) {
            if (!plugin.shops.containsKey(villagerName)) {
                return;
            }
            YamlConfiguration shop = plugin.shops.get(villagerName);
            DSSFunction.openShop(p, villagerName, p.getName());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory() instanceof DInventory) {
            DInventory di = (DInventory) e.getInventory();
            if (di.isValidHandler(plugin)) {
                if (!plugin.currentEditShop.containsKey(p.getUniqueId())) {
                    plugin.currentItem.remove(p.getUniqueId());
                    plugin.isBuying.remove(p.getUniqueId());
                    return;
                }
                if (e.getView().getTitle().equals(plugin.currentEditShop.get(p.getUniqueId()))) {
                    DSSFunction.saveShopShowCase(p, di);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() instanceof DInventory) {
            if (plugin.currentEditShop.containsKey(p.getUniqueId())) return;
            DInventory di = (DInventory) e.getInventory();
            if (di.isValidHandler(plugin)) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
                        di.prevPage();
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
                        di.nextPage();
                        return;
                    }
                    ClickType ct = e.getClick();
                    if (ct.isLeftClick()) {
                        if (NBT.hasTagKey(e.getCurrentItem(), "price")) {
                            if (plugin.currentItem.containsKey(p.getUniqueId())) {
                                if (plugin.isBuying.containsKey(p.getUniqueId())) {
                                    if (plugin.isBuying.get(p.getUniqueId())) {
                                        DSSFunction.buyMultiple(p, plugin.currentItem.get(p.getUniqueId()), NBT.getIntegerTag(e.getCurrentItem(), "amount"));
                                        if(!plugin.preventInvClose) {
                                            p.closeInventory();
                                            plugin.currentItem.remove(p.getUniqueId());
                                            plugin.isBuying.remove(p.getUniqueId());
                                        }
                                        return;
                                    } else {
                                        DSSFunction.sellMultiple(p, plugin.currentItem.get(p.getUniqueId()), NBT.getIntegerTag(e.getCurrentItem(), "amount"), false);
                                        if(!plugin.preventInvClose) {
                                            p.closeInventory();
                                            plugin.currentItem.remove(p.getUniqueId());
                                            plugin.isBuying.remove(p.getUniqueId());
                                        }
                                        return;
                                    }
                                }
                            }
                            DSSFunction.openBuyOption(p, e.getCurrentItem(), true);
                            plugin.currentItem.put(p.getUniqueId(), e.getCurrentItem());
                            plugin.isBuying.put(p.getUniqueId(), true);
                        }
                        return;
                    }
                    if(ct.isCreativeAction()) {
                        if (NBT.hasTagKey(e.getCurrentItem(), "sellPrice")) {
                            plugin.currentItem.put(p.getUniqueId(), e.getCurrentItem());
                            plugin.isBuying.put(p.getUniqueId(), false);
                            DSSFunction.sellMultiple(p, plugin.currentItem.get(p.getUniqueId()), NBT.getIntegerTag(e.getCurrentItem(), "amount"), true);
                            return;
                        }
                    }
                    if (ct.isRightClick()) {
                        if (NBT.hasTagKey(e.getCurrentItem(), "sellPrice")) {
                            if (plugin.currentItem.containsKey(p.getUniqueId())) return;
                            DSSFunction.openBuyOption(p, e.getCurrentItem(), false);
                            plugin.currentItem.put(p.getUniqueId(), e.getCurrentItem());
                            plugin.isBuying.put(p.getUniqueId(), false);
                        }
                    }
                }
            }
        }
    }
}
