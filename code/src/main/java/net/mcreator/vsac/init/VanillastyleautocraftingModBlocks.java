
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.vsac.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.vsac.block.AutoCrafterBlock;
import net.mcreator.vsac.VanillastyleautocraftingMod;

public class VanillastyleautocraftingModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, VanillastyleautocraftingMod.MODID);
	public static final RegistryObject<Block> AUTO_CRAFTER = REGISTRY.register("auto_crafter", () -> new AutoCrafterBlock());
}
