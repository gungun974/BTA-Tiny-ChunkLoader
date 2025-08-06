package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin {
	@Shadow
	@Final
	private Set<ChunkCoordinate> positionsToUpdate;

	@Shadow
	public Dimension dimension;

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
	}
}
