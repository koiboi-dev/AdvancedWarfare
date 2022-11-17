package me.kaiyan.missilewarfare.blocks;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import me.kaiyan.missilewarfare.blocks.util.BlockPlaceHandlerWrapper;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class AbstractBlock extends SlimefunItem implements Block {
    public AbstractBlock(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler((BlockUseHandler) this::onBlockRightClick);
        addItemHandler(new BlockPlaceHandlerWrapper(false,
                                                    this::onPlayerPlace)); // is there a way I could just feed onPlayerPlace here ?????
    }

    @Override
    public abstract boolean isSynchronized();

    @Override
    public abstract void tick(org.bukkit.block.Block block, SlimefunItem slimefunItem, Config config);

    @Override
    public abstract void onBlockRightClick(PlayerRightClickEvent event);

    @Override
    public abstract void onPlayerPlace(BlockPlaceEvent event);

    @Override
    public abstract Material getSupportBlock();

    @Override
    public boolean checkSupportBlock(BlockPlaceEvent event) {
        World world = event.getBlock().getWorld();
        org.bukkit.block.Block below = world.getBlockAt(event.getBlock().getLocation().subtract(new Vector(0, 1, 0)));
        return (below.getType() == this.getSupportBlock());
    }
}
