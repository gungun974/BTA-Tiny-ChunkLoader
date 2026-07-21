package gungun974.tinychunkloader.core;

import gungun974.tinychunkloader.cc.turtle.TinyChunkLoaderTurtleUpgrades;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.util.dependency.Key;

public class TinyChunkLoader implements ModInitializer {
    public static final String MOD_ID = HalpLibe.registerMod("tinychunkloader");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static int startBlockID = 1910;

	public static int GLOBAL_CHUNK_LOAD_LIMIT = 1024;
	public static int PLAYER_CHUNK_LOAD_LIMIT = 1024;

	public static boolean ENABLE_CHUNKLOADER_BLOCK = true;
	public static boolean ENABLE_CHUNKLOADER_MINECART = true;
	public static boolean ENABLE_CHUNKLOADER_TURTLE = true;

	public static boolean ENABLE_CHUNKLOADER_BLOCK_CRAFT = true;
	public static boolean ENABLE_CHUNKLOADER_MINECART_CRAFT = true;
	public static boolean ENABLE_CHUNKLOADER_TURTLE_CRAFT = true;

	public static int CHUNKLOADER_BLOCK_RANGE = 2;
	public static int CHUNKLOADER_MINECART_RANGE = 2;
	public static int CHUNKLOADER_TURTLE_RANGE = 2;

	@Override
    public void onInitialize() {
		TinyChunkLoaderConfig.RegisterConfig();
		TinyChunkLoaderBlocks.RegisterBlocks();
        LOGGER.info("TinyChunkLoader initialized.");

		CommonEvents.RECIPES_NAMESPACE_INIT.listen(Key.of(MOD_ID), () -> new TinyChunkLoaderRecipe().initNamespaces());
		CommonEvents.RECIPES_READY.listen(Key.of(MOD_ID), () -> new TinyChunkLoaderRecipe().onRecipesReady());

		CommonEvents.AFTER_GAME_START.listen(Key.of(MOD_ID), () -> {
			try {
				Class.forName("dan200.computercraft.api.ComputerCraftAPI");
				registerTurtleUpgrades();
			} catch (ClassNotFoundException ignored) {
			}
		});
    }

	public static void registerServerCommands() {
		CommandManager.registerCommand(new TinyChunkLoaderCommands());
	}

	public static void registerClientCommands() {
		if (EnvironmentHelper.isSingleplayerClient()) {
			CommandManager.registerCommand(new TinyChunkLoaderCommands());
		}
	}

	private void registerTurtleUpgrades() {
		TinyChunkLoaderTurtleUpgrades.registerTurtleUpgrades();
	}
}
