package gungun974.tinychunkloader.core;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.ItemStack;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class TinyChunkLoaderRecipe implements RecipeEntrypoint {

	public static TinyChunkLoaderRecipeNamespace TINYCHUNKLOADER = new TinyChunkLoaderRecipeNamespace();
	public static RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH;

	@Override
	public void onRecipesReady() {
		TinyChunkLoader.LOGGER.info("Loading TinyChunkLoader recipes...");
		resetGroups();
		registerNamespaces();
		load();
	}

	@Override
	public void initNamespaces() {
		TinyChunkLoader.LOGGER.info("Loading TinyChunkLoader recipe namespaces...");
		resetGroups();

		registerNamespaces();
	}

	public void registerNamespaces() {
		TINYCHUNKLOADER.register("workbench", WORKBENCH);
		Registries.RECIPES.register("tinychunkloader", TINYCHUNKLOADER);
	}

	public void resetGroups() {
		WORKBENCH = new RecipeGroup<RecipeEntryCrafting<?, ?>>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
		Registries.RECIPES.unregister("tinychunkloader");
	}

	public void load() {
		DataLoader.loadRecipesFromFile("/assets/tinychunkloader/recipes/workbench.json");

		TinyChunkLoader.LOGGER.info("{} recipes in {} groups.", TINYCHUNKLOADER.getAllRecipes().size(), TINYCHUNKLOADER.size());
	}
}

