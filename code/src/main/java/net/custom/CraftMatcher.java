package net.custom;

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

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import common.PairContainer;


public class CraftMatcher {

	public static Boolean isEmptyItemStack(ItemStack itemstack){
		return ( itemstack.getItem().equals((ItemStack.EMPTY).getItem()) );
	}

	public static Boolean matchWithIngredient(ItemStack item, Ingredient ingredient) {

		Boolean res = false;
		
		if (ingredient.isEmpty()){
			res = isEmptyItemStack(item);
		}
		else{

			ItemStack[] variants = ingredient.getItems();

			int i = 0;
			while(i < variants.length && !res){

				res = ( item.getItem().equals(variants[i].getItem()) );

				i+=1;
			}
		}

		return res;
		
	}

	public static Boolean matchRecipes(CraftingRecipe recipe, ItemStack[] invArr, MinecraftServer _mcserv) throws Exception {

		Boolean res = false;
		
		if (recipe instanceof ShapedRecipe){
			//_mcserv.getPlayerList().broadcastSystemMessage(Component.literal("shaped" +ingredients.size() + " " + invArr), false);
			res = matchShapedRecipes(recipe,invArr);
			
		}
		else if (recipe instanceof ShapelessRecipe){
			res = matchShapelessRecipes(recipe,invArr);
			//_mcserv.getPlayerList().broadcastSystemMessage(Component.literal("END shapeless"), false);
		}
		else{ 
			//throw new Exception("Error : Recipe is nor Shaped nor Shapeless");
			// Not handeled
		}

		return res;
	}

	public static Boolean checkIfEmptyItemStackArr(ItemStack[] invArr,List<Integer> selection){

		int i = 0; Boolean fail = false;
		while(i<selection.size() && !fail){

			fail = !isEmptyItemStack(invArr[selection.get(i)]);

			i+=1;
		}

		return !fail;
		
	}

	public static HashSet<Integer> tryShaveOffLines(ItemStack[] invArr){

		HashSet<Integer> slotsToRem = new HashSet<Integer>();

		int line = 0;

		List<Integer> lineElems = new ArrayList<Integer>();

		for (int i = 0; i <= invArr.length; i++){ // i <= ok since will just begin "new line"

			if ( ( (int) (i / 3) ) != line ){

				// check old line
				Boolean isEmpty = checkIfEmptyItemStackArr(invArr,lineElems);
				if (isEmpty){
					for (int j = 0; j < lineElems.size(); j++){
						slotsToRem.add(lineElems.get(j));
					}
				}
				
				lineElems = new ArrayList<Integer>();
				
				line += 1;
			}
			lineElems.add(i);
		}

		return slotsToRem;
		
	}

	public static HashSet<Integer> tryShaveOffColumns(ItemStack[] invArr){

		HashSet<Integer> slotsToRem = new HashSet<Integer>();
		ArrayList<ArrayList<Integer>> columnElems = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> newList;
		for (int i = 0; i < invArr.length; i++){

			if (i<3){
				newList = new ArrayList<Integer>();
				newList.add(i);
				columnElems.add(newList);
			}
			else{columnElems.get(i%3).add(i);}
		
		}

		for (int c = 0; c < columnElems.size(); c++ ){

			ArrayList<Integer> column = columnElems.get(c);

			Boolean isEmpty = checkIfEmptyItemStackArr(invArr,column);
			if (isEmpty){
				for (int j = 0; j < column.size(); j++){
					slotsToRem.add(column.get(j));
				}
			}
			
		}

		return slotsToRem;
		
	}

	public static PairContainer<ArrayList<ItemStack>, PairContainer<Integer,Integer>> tryShaveOff(ItemStack[] invArr){
		// suppossed 3x3 initially
		
		HashSet<Integer> lineSlotsToRem = tryShaveOffLines(invArr);
		HashSet<Integer> columnSlotsToRem = tryShaveOffColumns(invArr);

		Integer nbLines = 3 - lineSlotsToRem.size()/3;
		Integer nbColumns = 3 - columnSlotsToRem.size()/3;

		PairContainer<Integer,Integer> dims = new PairContainer(nbColumns,nbLines);

		List<ItemStack> res = new ArrayList<ItemStack>();

		for (int i = 0 ; i < invArr.length;i++ ){

			if ( !(lineSlotsToRem.contains(i) || columnSlotsToRem.contains(i)) ){
				res.add(invArr[i].copy());
			}
		}

		return new PairContainer(res,dims);
	}

	public static Boolean matchShapedRecipes(CraftingRecipe recipe, ItemStack[] invArr) {

		NonNullList<Ingredient> recipeIngredients =  recipe.getIngredients();

		// try removing empty lines and columns
		PairContainer<ArrayList<ItemStack>, PairContainer<Integer,Integer>> resShave = tryShaveOff(invArr);

		ArrayList<ItemStack> cleanShave = resShave.getKey();
		PairContainer<Integer,Integer> dims = resShave.getValue();

		ItemStack[] cleanInvArr = cleanShave.toArray(new ItemStack[0]);

		Boolean fail = false;
		int nbItems = cleanInvArr.length;

		if (recipe.canCraftInDimensions(dims.getKey(),dims.getValue()) && recipeIngredients.size() == nbItems){
			int i = 0;
			while (i < nbItems && !fail) {
				fail = !matchWithIngredient(cleanInvArr[i], recipeIngredients.get(i));
				i+=1;
			}
		}
		else{fail = true;}

		return !fail;
		
	}

	public static Boolean matchShapelessRecipes(CraftingRecipe recipe, ItemStack[] invArr) {

		NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();

		Boolean fail = false;
		
		List<ItemStack> realInvArr = new ArrayList<ItemStack>();
		for (int i=0;i<invArr.length;i++){
			ItemStack item = invArr[i];
			if ( !isEmptyItemStack(item) ){realInvArr.add(item.copy());}
		}

		int realNbItems = realInvArr.size();

		if (realNbItems != recipeIngredients.size()){fail = true;}
		else{

			// for item try and find maching ingredient
			HashSet<Integer> usedIngredients = new HashSet<Integer>();

			int j; Boolean found;
			int i = 0;
			while(i<realNbItems && !fail){

				ItemStack realItem = realInvArr.get(i);

				j = 0; found = false;
				while(j<realNbItems && !found){

					if ( !usedIngredients.contains(j) ){
						found = matchWithIngredient(realItem, recipeIngredients.get(j));
						if (found){usedIngredients.add(j);}
					}
					
					j+=1;
				}

				fail = !found;
					
				i+=1;	
			}
			
		}
		
		return !fail;
		
	}

	public static void displayRecipe(MinecraftServer _mcserv, CraftingRecipe recipe) {
		
		NonNullList<Ingredient> ingredients =  recipe.getIngredients();

		for (int i = 0;i<ingredients.size();i++){
			Ingredient ingre = ingredients.get(i);

			if (ingre.isEmpty()){_mcserv.getPlayerList().broadcastSystemMessage(Component.literal( "ELEM " + String.valueOf(i) + " = empty" ), false);}
			else{
				ItemStack elem = ingre.getItems()[0];
				_mcserv.getPlayerList().broadcastSystemMessage(Component.literal( "ELEM " + String.valueOf(i) + " = " + elem.getItem() ), false);
				
			}

		}
		if (recipe instanceof ShapedRecipe){
			_mcserv.getPlayerList().broadcastSystemMessage(Component.literal("shaped"), false);

		}
		else if (recipe instanceof ShapelessRecipe){
			_mcserv.getPlayerList().broadcastSystemMessage(Component.literal("shapeless"), false);
		}

	}

	
	public static ItemStack getRecipeResult(LevelAccessor world, BlockPos pos, MinecraftServer _mcserv, ItemStack[] invArr) {
		BlockEntity _ent = world.getBlockEntity(pos);
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		if (_ent != null){
			Level level = _ent.getLevel();
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
				if (capability instanceof IItemHandlerModifiable _modHandler){

					Container cont = (Container) new RecipeWrapper(_modHandler);

					Iterator<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).iterator();
					
					Boolean found = false;
					while (recipes.hasNext() && !found) {

						CraftingRecipe recipe = recipes.next();
						try { found = matchRecipes(recipe, invArr,_mcserv);} 
						catch (Exception e) {
							Logger logger = LogUtils.getLogger();
							logger.error("error in matchRecipes ",e);
							_mcserv.getPlayerList().broadcastSystemMessage(Component.literal(e.toString()), false);
						}

						if (found){ _retval.set(recipe.getResultItem().copy()); }
						
					}
				}
			});
		}
		return _retval.get();
		
	}
	
	/*

	public static ItemStack getRecipeResult(LevelAccessor world, BlockPos pos, MinecraftServer _mcserv, ItemStack[] invArr) {
		BlockEntity _ent = world.getBlockEntity(pos);
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		if (_ent != null){
			Level level = _ent.getLevel();
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
				if (capability instanceof IItemHandlerModifiable _modHandler){

					//Container cont = (Container) new RecipeWrapper(_modHandler);

					CraftingContainer cc = new CraftingContainer(null,3,3);

					for (int i = 0; i < invArr.length;i++){ cc.setItem(i, invArr[i]); }

					Optional<CraftingRecipe> optionalRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, cc, level); // fails due to null menu

					 if (optionalRecipe.isPresent()){

					 	CraftingRecipe recipe = optionalRecipe.get();

					 	if (recipe != null) {
							_retval.set(recipe.getResultItem().copy());
						}
					 	
					 }

					
					
				}
			});
		}
		return _retval.get();
		
	}
	*/


}
