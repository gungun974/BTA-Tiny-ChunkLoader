package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ItemToolPickaxe.class, remap = false)
public class ItemToolPickaxeMixin {
	@Shadow
	public static Map<Block<?>, Integer> miningLevels;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void injectMiningLevel(CallbackInfo ci) {
		miningLevels.put(TinyChunkLoaderBlocks.CHUNKLOADER, 3);

	}
}
