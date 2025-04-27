package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.RememberChunkToLoad;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.SaveHandlerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SaveHandlerBase.class, remap = false)
public class SaveHandlerBaseMixin {
	@Inject(method = "getLevelData", at = @At("TAIL"))
	void loadTinyChunkloader(CallbackInfoReturnable<LevelData> cir) {
		RememberChunkToLoad rememberChunkToLoad = (RememberChunkToLoad) cir.getReturnValue();

		if (rememberChunkToLoad == null) {
			return;
		}

		ChunkLoaderManager.getInstance().setDimensionToLoads(
			rememberChunkToLoad.tinyChunkLoader$getLocalDimensionsToLoads()
		);
	}
}
