package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.Map;
import java.util.Set;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin {
	@Shadow
	@Final
	private Set<ChunkCoordinate> positionsToUpdate;

	@Shadow
	public Dimension dimension;

	@Shadow
	public abstract ChunkCoordinates getSpawnPoint();

	@Inject(method = "tick", at = @At("TAIL"))
	void keepChunkLoaded(CallbackInfo ci) {
		ChunkLoaderManager.getInstance().update((World) (Object) this);
	}

	@Inject(method = "updateBlocksAndPlayCaveSounds", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", ordinal = 0, shift = At.Shift.AFTER))
	void updateBlocksInChunkLoadedChunks(CallbackInfo ci) {
		Map<ChunkCoordinate, Integer> chunks = ChunkLoaderManager.getInstance().getDimensionToLoads().get(dimension);

		if (chunks != null) {
			positionsToUpdate.addAll(
				chunks.keySet()
			);
		}

		// Also include servers natural loaded chunks

		if (EnvironmentHelper.isServerEnvironment()) {
			ChunkCoordinates spawnCords = getSpawnPoint();
			int spawnChunkX = spawnCords.x >> 4;
			int spawnChunkZ = spawnCords.z >> 4;

			int radiusChunks = 128 / 16; // 128 is the hard coded distance of the server

			for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
				for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
					int chunkX = spawnChunkX + dx;
					int chunkZ = spawnChunkZ + dz;
					positionsToUpdate.add(new ChunkCoordinate(chunkX, chunkZ));
				}
			}
		}
	}
}
