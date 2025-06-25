package gungun974.tinychunkloader.core;

import gungun974.tinychunkloader.cc.turtle.TinyChunkLoaderTurtleUpgrades;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class TinyChunkLoader implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "tinychunkloader";
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
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {
		try {
			Class.forName("dan200.computercraft.api.ComputerCraftAPI");
			registerTurtleUpgrades();
		} catch (ClassNotFoundException ignored) {
		}
	}

	private void registerTurtleUpgrades() {
		TinyChunkLoaderTurtleUpgrades.registerTurtleUpgrades();
	}
}
