package net.mcreator.vsac.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.concurrent.atomic.AtomicReference;
import java.util.Optional;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

import java.lang.Boolean;
import java.lang.String;
import java.lang.Exception;
import java.lang.Integer;

import net.custom.CraftMatcher;

public class AutoCrafterRedstoneOnProcedure {

	public static ItemStack getInvItemStack(LevelAccessor world, BlockPos pos, int slotid) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		BlockEntity _ent = world.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
					.ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).copy()));
		return _retval.get();
	}

	public static void setInvItemStack(LevelAccessor world, BlockPos pos, int slotid, ItemStack _setstack) {
		BlockEntity _ent = world.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
				if (capability instanceof IItemHandlerModifiable _modHandler)
					_modHandler.setStackInSlot(slotid, _setstack);
			});
		
	}

	public static void execute(LevelAccessor world, double x, double y, double z) {
		int invSize = 9;
		ItemStack[] invArr = new ItemStack[invSize];
		if (!world.isClientSide()) {
			MinecraftServer _mcserv = ServerLifecycleHooks.getCurrentServer();
			
			if (_mcserv != null) {
				BlockPos pos = new BlockPos(x, y, z);
				
				if ( CraftMatcher.isEmptyItemStack(getInvItemStack(world, pos, 9)) ){ // if result slot empty
			
					for (int i=0;i<invSize;i++){
						invArr[i] = getInvItemStack(world, new BlockPos(x, y, z), i);
						//_mcserv.getPlayerList().broadcastSystemMessage(Component.literal( "Powered elem " + String.valueOf(i) + " = " + invArr[i] ), false);
					}

					final ItemStack item = CraftMatcher.getRecipeResult(world, pos,_mcserv, invArr);
					//_mcserv.getPlayerList().broadcastSystemMessage(Component.literal( "RES: " + item), false);

					if ( !CraftMatcher.isEmptyItemStack(item) ){
						final ItemStack _setstack = item;
						
						setInvItemStack(world, pos, 9, _setstack);

						for (int i=0;i<invSize;i++){
							setInvItemStack(world, pos, i, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}
	
}
