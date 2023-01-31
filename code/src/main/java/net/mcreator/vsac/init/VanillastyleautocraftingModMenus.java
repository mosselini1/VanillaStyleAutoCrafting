
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.vsac.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.vsac.world.inventory.AutoCrafterGuiMenu;
import net.mcreator.vsac.VanillastyleautocraftingMod;

public class VanillastyleautocraftingModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			VanillastyleautocraftingMod.MODID);
	public static final RegistryObject<MenuType<AutoCrafterGuiMenu>> AUTO_CRAFTER_GUI = REGISTRY.register("auto_crafter_gui",
			() -> IForgeMenuType.create(AutoCrafterGuiMenu::new));
}
