package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.Map;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin {
	@Shadow
	@Final
	private LongOpenHashSet positionsToUpdate;

	@Shadow
	public Dimension dimension;

	@Shadow
	public abstract TilePos getSpawnPoint();

	@Inject(method = "tick", at = @At("TAIL"))
	void keepChunkLoaded(CallbackInfo ci) {
		ChunkLoaderManager.getInstance().update((World) (Object) this);
	}

	@Inject(method = "updateBlocksAndPlayCaveSounds", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/LongOpenHashSet;clear()V", ordinal = 0, shift = At.Shift.AFTER))
	void updateBlocksInChunkLoadedChunks(CallbackInfo ci) {
		Map<ChunkCoordinate, Integer> chunks = ChunkLoaderManager.getInstance().getDimensionToLoads().get(dimension);

		if (chunks != null) {
			for (ChunkCoordinate coordinate : chunks.keySet()) {
				positionsToUpdate.add(packChunkPos(coordinate.x, coordinate.z));
			}
		}

		if (EnvironmentHelper.isMultiplayerServer()) {
			TilePos spawnCords = getSpawnPoint();
			int spawnChunkX = spawnCords.x >> 4;
			int spawnChunkZ = spawnCords.z >> 4;

			int radiusChunks = 128 / 16;

			for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
				for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
					positionsToUpdate.add(packChunkPos(spawnChunkX + dx, spawnChunkZ + dz));
				}
			}
		}
	}

	private static long packChunkPos(int chunkX, int chunkZ) {
		return (long) chunkX & 4294967295L | ((long) chunkZ & 4294967295L) << 32;
	}
}
