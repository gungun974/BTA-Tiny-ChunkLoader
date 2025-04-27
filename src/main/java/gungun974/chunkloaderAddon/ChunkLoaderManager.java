package gungun974.chunkloaderAddon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.world.chunk.provider.ChunkProviderDynamic;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import turniplabs.halplibe.helper.EnvironmentHelper;

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

	public Map<Dimension, Map<ChunkCoordinate, Integer>> getDimensionToLoads() {
		return dimensionToLoads;
	}

	private final Map<Dimension, Map<ChunkCoordinate, Integer>> dimensionToLoads = new HashMap<>();

	synchronized public void keepChunkLoaded(int chunkX, int chunkZ, World world) {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);

		Map<ChunkCoordinate, Integer> chunkToLoads = dimensionToLoads.getOrDefault(world.dimension, new HashMap<>());

		chunkToLoads.put(coordinate, 0);

		dimensionToLoads.put(world.dimension, chunkToLoads);
	}

	static int PING_TIMEOUT = 10;

	synchronized public void update(World world) {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		Map<ChunkCoordinate, Integer> chunkToLoads = dimensionToLoads.getOrDefault(world.dimension, new HashMap<>());
		Map<ChunkCoordinate, Integer> updatedChunkToLoads = new HashMap<>(chunkToLoads);

		IChunkProvider chunkProvider = world.getChunkProvider();

		for (Map.Entry<ChunkCoordinate, Integer> entry : chunkToLoads.entrySet()) {
			ChunkCoordinate coordinate = entry.getKey();
			int currentPing = entry.getValue();

			if (currentPing < PING_TIMEOUT)  {
				updatedChunkToLoads.put(coordinate, currentPing + 1);

				if (EnvironmentHelper.isSinglePlayer()) {
					loadChunkForSP(chunkProvider, coordinate);
				} else {
					loadChunkForMP(chunkProvider, coordinate);
				}
			} else {
				updatedChunkToLoads.remove(coordinate);

				if (EnvironmentHelper.isSinglePlayer()) {
					unloadChunkForSP(chunkProvider, coordinate);
				} else {
					unloadChunkForMP(chunkProvider, coordinate);
				}
			}
		}

		dimensionToLoads.put(world.dimension, updatedChunkToLoads);
	}

	@Environment(EnvType.CLIENT)
	private static void unloadChunkForSP(IChunkProvider chunkProvider, ChunkCoordinate coordinate) {
		if (chunkProvider instanceof ChunkProviderDynamic) {
			((ChunkProviderDynamic) chunkProvider).removeFromForceLoaded(coordinate.x, coordinate.z);
		}
	}

	@Environment(EnvType.CLIENT)
	private static void loadChunkForSP(IChunkProvider chunkProvider, ChunkCoordinate coordinate) {
		if (chunkProvider instanceof ChunkProviderDynamic) {
			((ChunkProviderDynamic) chunkProvider).keepLoaded(coordinate.x, coordinate.z);
		}
	}

	@Environment(EnvType.SERVER)
	private static void unloadChunkForMP(IChunkProvider chunkProvider, ChunkCoordinate coordinate) {
		if (chunkProvider instanceof ChunkProviderServer) {
			((ChunkProviderServer) chunkProvider).dropChunk(coordinate.x, coordinate.z);
		}
	}

	@Environment(EnvType.SERVER)
	private static void loadChunkForMP(IChunkProvider chunkProvider, ChunkCoordinate coordinate) {
		if (chunkProvider instanceof ChunkProviderServer) {
			((ChunkProviderServer) chunkProvider).provideChunk(coordinate.x, coordinate.z);
		}
	}
}
