package gungun974.tinychunkloader.helpers;

import gungun974.tinychunkloader.core.ChunkProviderDynamic2;
import gungun974.tinychunkloader.core.TinyChunkLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.*;

public class ChunkLoaderManager {

	private static ChunkLoaderManager instance;
	private Map<Dimension, Map<ChunkCoordinate, Integer>> dimensionToLoads = new HashMap<>();
	private final Map<UUID, Map<Dimension, Set<ChunkCoordinate>>> playerDimensionsChunks = new HashMap<>();

	private final Map<UUID, Integer> totalPlayerChunkLoaded = new HashMap<>();

	private ChunkLoaderManager() {}

	public long stableCurrentTotalLoads = 0;
	private Map<UUID, Integer> stableTotalPlayerChunkLoaded = new HashMap<>();

	public static synchronized ChunkLoaderManager getInstance() {
		if (instance == null) {
			instance = new ChunkLoaderManager();
		}
		return instance;
	}

	public Map<Dimension, Map<ChunkCoordinate, Integer>> getDimensionToLoads() {
		return dimensionToLoads;
	}

	public void setDimensionToLoads(Map<Dimension, Map<ChunkCoordinate, Integer>> dimensionToLoads) {
		this.dimensionToLoads = dimensionToLoads;
	}

	synchronized public boolean keepChunkLoaded(int chunkX, int chunkZ, World world, UUID owner) {
		if (EnvironmentHelper.isClientWorld()) {
			return false;
		}

		final long totalLoaded = dimensionToLoads.values().stream().flatMap(m -> m.values().stream()).mapToLong(value -> {
			if (value == 0) {
				return 1;
			}
			return 0;
		}).sum();

		if (totalLoaded + 1 > TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT) {
			return false;
		}

		ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);

		Map<ChunkCoordinate, Integer> chunkToLoads = dimensionToLoads.getOrDefault(world.dimension, new HashMap<>());

		if (totalPlayerChunkLoaded.getOrDefault(owner, 0) + 1 > TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT) {
			if (chunkToLoads.get(coordinate) == null) {
				totalPlayerChunkLoaded.put(owner, totalPlayerChunkLoaded.getOrDefault(owner, 0) + 1);
			}
			return false;
		}

		Map<Dimension, Set<ChunkCoordinate>> playerDimensions = playerDimensionsChunks.getOrDefault(owner, new HashMap<>());
		Set<ChunkCoordinate> playerChunks = playerDimensions.getOrDefault(world.dimension, new HashSet<>());

		if (playerDimensionsChunks.values().stream().map(m -> m.get(world.dimension)).filter(Objects::nonNull).noneMatch(set -> set.contains(coordinate))) {
			playerChunks.add(coordinate);

			playerDimensions.put(world.dimension, playerChunks);

			playerDimensionsChunks.put(owner, playerDimensions);
		}

		if (playerChunks.contains(coordinate) && chunkToLoads.getOrDefault(coordinate, -1) != 0) {
			totalPlayerChunkLoaded.put(owner, totalPlayerChunkLoaded.getOrDefault(owner, 0) + 1);
		}

		chunkToLoads.put(coordinate, 0);

		dimensionToLoads.put(world.dimension, chunkToLoads);

		return true;
	}

	static int PING_TIMEOUT = 10;

	synchronized public void update(World world) {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}


		this.stableCurrentTotalLoads = dimensionToLoads.values().stream().flatMap(m -> m.values().stream()).mapToLong(value -> {
			if (value == 0) {
				return 1;
			}
			return 0;
		}).sum();

		this.stableTotalPlayerChunkLoaded = new HashMap<>(totalPlayerChunkLoaded);

		totalPlayerChunkLoaded.forEach((uuid, count) -> totalPlayerChunkLoaded.put(uuid, 0));

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

				playerDimensionsChunks.values().forEach(map -> {
					Set<ChunkCoordinate> chunks = map.get(world.dimension);
					if (chunks != null) {
						chunks.remove(coordinate);
					}
				});

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
		if (chunkProvider instanceof ChunkProviderDynamic2) {
			((ChunkProviderDynamic2) chunkProvider).removeFromForceLoaded(coordinate.x, coordinate.z);
		}
	}

	@Environment(EnvType.CLIENT)
	private static void loadChunkForSP(IChunkProvider chunkProvider, ChunkCoordinate coordinate) {
		if (chunkProvider instanceof ChunkProviderDynamic2) {
			((ChunkProviderDynamic2) chunkProvider).keepLoaded(coordinate.x, coordinate.z);
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
			((ChunkProviderServer) chunkProvider).prepareChunk(coordinate.x, coordinate.z);
		}
	}

	public long getCurrentTotalLoads() {
		return stableCurrentTotalLoads;
	}

	public long getCurrentPlayerTotalLoads(UUID player) {
		return stableTotalPlayerChunkLoaded.get(player);
	}

	public Set<UUID> getCurrentPlayers() {
		return stableTotalPlayerChunkLoaded.keySet();
	}

	public Map<Dimension, Set<ChunkCoordinate>> getCurrentPlayerChunks(UUID player) {
		return playerDimensionsChunks.get(player);
	}
}
