package me.kaiyan.missilewarfare.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import me.kaiyan.missilewarfare.MissileWarfare;
import me.kaiyan.missilewarfare.missiles.MissileController;
import me.kaiyan.missilewarfare.util.Translations;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MissileRadar extends SlimefunItem {
    public MissileRadar(ItemGroup itemGroup, ItemStack[] recipe) {
        super(itemGroup, new SlimefunItemStack("MISSILERADAR", Material.GRAY_WOOL,
                                               ChatColor.YELLOW + Translations.get("radar.name"),
                                               ChatColor.GRAY + Translations.get("radar.lore")),
              RecipeType.ENHANCED_CRAFTING_TABLE, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return true;
            }

            @Override
            public void tick(Block block, SlimefunItem slimefunItem, Config config) {
                if (!MissileWarfare.activemissiles.isEmpty()) {
                    boolean missilenear = false;
                    for (MissileController missile : MissileWarfare.activemissiles) {
                        if (block.getLocation().distanceSquared(missile.pos.toLocation(missile.world)) < (700 * 700)) {
                            missilenear = true;
                            break;
                        }
                    }
                    if (block.getRelative(BlockFace.UP).getType() == Material.REDSTONE_WIRE && missilenear) {
                        RedstoneWire wire = (RedstoneWire) block.getRelative(BlockFace.UP).getBlockData();
                        wire.setPower(wire.getMaximumPower());
                        block.getRelative(BlockFace.UP).setBlockData(wire);
                        MissileWarfare
                                .getInstance()
                                .getServer()
                                .getPluginManager()
                                .callEvent(new BlockRedstoneEvent(block.getRelative(BlockFace.UP), wire.getPower(),
                                                                  wire.getMaximumPower()));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                RedstoneWire wire = (RedstoneWire) block.getRelative(BlockFace.UP).getBlockData();
                                wire.setPower(0);
                                block.getRelative(BlockFace.UP).setBlockData(wire);
                                MissileWarfare
                                        .getInstance()
                                        .getServer()
                                        .getPluginManager()
                                        .callEvent(
                                                new BlockRedstoneEvent(block.getRelative(BlockFace.UP), wire.getPower(),
                                                                       0));
                            }
                        }.runTaskLater(MissileWarfare.getInstance(), 1);
                    }
                }
            }
        });

        BlockPlaceHandler placeHandler = new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent event) {
                event.getPlayer().sendMessage(Translations.get("messages.radar"));
            }
        };
        addItemHandler(placeHandler);
    }
}
