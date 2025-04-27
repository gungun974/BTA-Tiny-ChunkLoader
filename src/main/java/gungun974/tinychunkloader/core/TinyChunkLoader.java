package gungun974.tinychunkloader.core;

import gungun974.tinychunkloader.cc.turtle.TinyChunkLoaderTurtleUpgrades;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class TinyChunkLoader implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "tinychunkloader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static int startBlockID = 1910;

	@Override
    public void onInitialize() {
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
