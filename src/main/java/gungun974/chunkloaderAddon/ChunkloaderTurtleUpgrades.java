package gungun974.chunkloaderAddon;

import dan200.computercraft.api.ComputerCraftAPI;
import gungun974.chunkloaderAddon.turtle.upgrades.TurtleChunkloader;
import net.minecraft.core.block.Blocks;

public class ChunkloaderTurtleUpgrades {
	public static TurtleChunkloader turtleChunkloader = new TurtleChunkloader(Blocks.BLOCK_DIAMOND.id());

	public static void registerTurtleUpgrades() {
		ComputerCraftAPI.registerTurtleUpgrade(turtleChunkloader);
	}
}

