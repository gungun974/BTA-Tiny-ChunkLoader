package gungun974.chunkloaderAddon.mixin;

import gungun974.chunkloaderAddon.ChunkLoaderManager;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class, remap = false)
public class WorldMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	void keepChunkLoaded(CallbackInfo ci) {
		ChunkLoaderManager.getInstance().update((World) (Object) this);
	}
}
