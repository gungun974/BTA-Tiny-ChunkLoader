package gungun974.tinychunkloader;

import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkCoordinate;

import java.util.Map;

public interface RememberChunkToLoad {
	Map<Dimension, Map<ChunkCoordinate, Integer>> tinyChunkLoader$getLocalDimensionsToLoads();
}
