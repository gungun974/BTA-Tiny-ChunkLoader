package gungun974.tinychunkloader.cc.turtle;

import dan200.computercraft.api.ComputerCraftAPI;
import gungun974.tinychunkloader.cc.turtle.upgrades.TurtleChunkloader;
import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;

public class TinyChunkLoaderTurtleUpgrades {
	public static TurtleChunkloader turtleChunkloader = new TurtleChunkloader(TinyChunkLoaderBlocks.CHUNKLOADER.id());

	public static void registerTurtleUpgrades() {
		ComputerCraftAPI.registerTurtleUpgrade(turtleChunkloader);
	}
}

