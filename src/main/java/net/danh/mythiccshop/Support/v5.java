package net.danh.mythiccshop.Support;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.danh.mythiccshop.Data.ShopManger;
import net.danh.mythiccshop.MythiccShop;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

import static net.danh.dcore.Utils.Player.sendPlayerMessage;
import static net.danh.mythiccshop.File.Files.getlanguagefile;

public class v5 implements ShopManger {

    @Override
    public boolean consumeItem(Player player, int count, ItemStack mat) {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

        int found = 0;
        for (ItemStack stack : ammo.values()) {
            found += stack.getAmount();
        }
        if (count > found) {
            sendPlayerMessage(player, Objects.requireNonNull(getlanguagefile().getString("NOT_ENOUGH_ITEM"))
                    .replaceAll("%item%", mat.getItemMeta().getDisplayName()));
            return false;
        }

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed) {
                player.getInventory().setItem(index, null);
            } else {
                stack.setAmount(stack.getAmount() - removed);
            }

            if (count <= 0) {
                break;
            }
        }
        player.updateInventory();
        return true;
    }

    @Override
    public void sellMythiccItem(Player p, String type, Integer price, Integer amount) {
        if (consumeItem(p, amount, MythicBukkit.inst().getItemManager().getItemStack(type))) {
            EconomyResponse e = MythiccShop.getEconomy().depositPlayer(p, price * amount);
            if (e.transactionSuccess()) {
                sendPlayerMessage(p, Objects.requireNonNull(getlanguagefile().getString("SELL_ITEMS"))
                        .replaceAll("%item%", MythicBukkit.inst().getItemManager().getItemStack(type).getItemMeta().getDisplayName())
                        .replaceAll("%price%", String.format("%,d", price))
                        .replaceAll("%amount%", String.format("%,d", amount)));
            }
        }
    }

    @Override
    public void buyMythiccItem(Player p, String type, Integer price, Integer amount) {
        if (MythiccShop.getEconomy().getBalance(p) >= price * amount) {
            EconomyResponse e = MythiccShop.getEconomy().withdrawPlayer(p, price * amount);
            p.getInventory().addItem(MythicBukkit.inst().getItemManager().getItemStack(type, amount));
            if (e.transactionSuccess()) {
                sendPlayerMessage(p, Objects.requireNonNull(getlanguagefile().getString("BUY_ITEMS"))
                        .replaceAll("%item%", MythicBukkit.inst().getItemManager().getItemStack(type).getItemMeta().getDisplayName())
                        .replaceAll("%price%", String.format("%,d", price))
                        .replaceAll("%amount%", String.format("%,d", amount)));
            }
        } else {
            sendPlayerMessage(p, Objects.requireNonNull(getlanguagefile().getString("NOT_ENOUGH_MONEY"))
                    .replaceAll("%money%", String.format("%,d", price * amount)));
        }
    }
}