package gungun974.chunkloaderAddon.mixin;

import gungun974.chunkloaderAddon.ChunkLoaderManager;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ChunkProviderServer.class, remap = false)
public class ChunkProviderServerMixin {
	@Shadow
	@Final
	private WorldServer world;

	@Inject(method = "dropChunk", at = @At("HEAD"), cancellable = true)
	public void protectLoadedChunk(int chunkX, int chunkZ, CallbackInfo ci) {
		Map<ChunkCoordinate, Integer> chunkToLoads = ChunkLoaderManager.getInstance().getDimensionToLoads().getOrDefault(world.dimension, new HashMap<>());

		for (Map.Entry<ChunkCoordinate, Integer> entry : chunkToLoads.entrySet()) {
			ChunkCoordinate coordinate = entry.getKey();

			if (chunkX == coordinate.x && chunkZ == coordinate.z) {
				ci.cancel();
				return;
			}
		}
	}

}
