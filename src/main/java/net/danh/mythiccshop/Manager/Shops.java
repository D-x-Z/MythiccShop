package net.danh.mythiccshop.Manager;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.danh.dcore.DCore;
import net.danh.dcore.Utils.Chat;
import net.danh.mythiccshop.File.Files;
import net.danh.mythiccshop.File.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.danh.dcore.Utils.Items.Lore;
import static net.danh.dcore.Utils.Items.makeItem;

public class Shops {

    public static void openShop(Player p, Shop shop) {
        FileConfiguration get = shop.getConfig();
        String name = Chat.colorize(Objects.requireNonNull(get.getString("NAME")));
        int size = get.getInt("SIZE") * 9;
        Inventory inv = Bukkit.createInventory(p, size, name);
        for (String item_name : Objects.requireNonNull(get.getConfigurationSection("ITEMS")).getKeys(false)) {
            if (get.contains("ITEMS." + item_name + ".MATERIAL")) {
                ItemStack item = makeItem(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".MATERIAL")))), Short.parseShort("0"), 1, get.getBoolean("ITEMS." + item_name + ".GLOW"), get.getBoolean("ITEMS." + item_name + ".HIDE_FLAG"), false, Objects.requireNonNull(get.getString("ITEMS." + item_name + ".NAME")), get.getStringList("ITEMS." + item_name + ".LORE"));

                if (get.contains("ITEMS." + item_name + ".SLOT")) {
                    int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                    inv.setItem(slot, item);
                }
                if (get.contains("ITEMS." + item_name + ".SLOTS")) {
                    for (Integer slots : get.getIntegerList("ITEMS." + item_name + ".SLOTS")) {
                        inv.setItem(slots, item);
                    }
                }
            }
            if (get.contains("ITEMS." + item_name + ".MYTHICC_TYPE")) {
                ItemStack mythicc_item = MythicBukkit.inst().getItemManager().getItemStack(get.getString("ITEMS." + item_name + ".MYTHICC_TYPE"));
                try {
                    ItemMeta meta = mythicc_item.getItemMeta();
                    List<String> lore_item = meta.getLore();
                    List<String> lore = new ArrayList<>();
                    List<String> lore_items = Files.getconfigfile().getStringList("LORE").stream().map(s -> s.replaceAll("%sell%", String.valueOf(get.get("ITEMS." + item_name + ".SELL_PRICE"))).replaceAll("%buy%", String.valueOf(get.getDouble("ITEMS." + item_name + ".BUY_PRICE")))).toList();
                    if (lore_item == null) {
                        lore.addAll(lore_items);
                    } else {
                        lore.addAll(lore_item);
                        lore.addAll(lore_items);
                    }
                    meta.setLore(Lore(lore));
                    mythicc_item.setItemMeta(meta);
                    int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                    inv.setItem(slot, mythicc_item);
                } catch (NullPointerException e) {
                    DCore.dCoreLog("&c" + e + " is null");
                }
            }
        }
        p.openInventory(inv);
    }
}
