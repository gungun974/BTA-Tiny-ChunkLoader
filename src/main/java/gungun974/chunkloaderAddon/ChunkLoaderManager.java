package gungun974.chunkloaderAddon;

import net.minecraft.client.world.chunk.provider.ChunkProviderDynamic;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.provider.IChunkProvider;

import java.util.HashMap;
import java.util.Map;

public class ChunkLoaderManager {

	private static ChunkLoaderManager instance;

	private ChunkLoaderManager() {}

	public static synchronized ChunkLoaderManager getInstance() {
		if (instance == null) {
			instance = new ChunkLoaderManager();
		}
		return instance;
	}

	private final Map<Dimension, Map<ChunkCoordinate, Integer>> dimensionToLoads = new HashMap<>();


	synchronized public void keepChunkLoaded(int chunkX, int chunkZ, World world) {
		ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);

		Map<ChunkCoordinate, Integer> chunkToLoads = dimensionToLoads.getOrDefault(world.dimension, new HashMap<>());

		chunkToLoads.put(coordinate, 0);

		dimensionToLoads.put(world.dimension, chunkToLoads);
	}

	static int PING_TIMEOUT = 10;

	synchronized public void update(World world) {
		Map<ChunkCoordinate, Integer> chunkToLoads = dimensionToLoads.getOrDefault(world.dimension, new HashMap<>());
		Map<ChunkCoordinate, Integer> updatedChunkToLoads = new HashMap<>(chunkToLoads);

		IChunkProvider chunkProvider = world.getChunkProvider();

		for (Map.Entry<ChunkCoordinate, Integer> entry : chunkToLoads.entrySet()) {
			ChunkCoordinate coordinate = entry.getKey();
			int currentPing = entry.getValue();

			if (currentPing < PING_TIMEOUT)  {
				updatedChunkToLoads.put(coordinate, currentPing + 1);

				if (chunkProvider instanceof ChunkProviderDynamic) {
					((ChunkProviderDynamic) chunkProvider).keepLoaded(coordinate.x, coordinate.z);
				}
			} else {
				updatedChunkToLoads.remove(coordinate);

				if (chunkProvider instanceof ChunkProviderDynamic) {
					((ChunkProviderDynamic) chunkProvider).removeFromForceLoaded(coordinate.x, coordinate.z);
				}
			}
		}

		dimensionToLoads.put(world.dimension, updatedChunkToLoads);
	}
}
