
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.vsac.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;

import net.mcreator.vsac.VanillastyleautocraftingMod;

public class VanillastyleautocraftingModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, VanillastyleautocraftingMod.MODID);
	public static final RegistryObject<Item> AUTO_CRAFTER = block(VanillastyleautocraftingModBlocks.AUTO_CRAFTER, CreativeModeTab.TAB_REDSTONE);

	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}
