package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.EntityRendererMinecart;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRendererMinecart.class, remap = false)
public class EntityRendererMinecartMixin {
	@Inject(
		method = "render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/vehicle/EntityMinecart;DDDFF)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/vehicle/EntityMinecart;getType()B", ordinal = 1)
	)
	public void displayChunkloader(TessellatorGeneral tessellator, EntityMinecart minecart, double x, double y, double z, float yaw, float partialTick, CallbackInfo ci) {
		if (minecart.getType() == 43) {
			GLRenderer.pushFrame();

			GLRenderer.enableState(State.BLEND);
			GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);

			(BlockModelDispatcher.getInstance().getDispatch(TinyChunkLoaderBlocks.CHUNKLOADER)).renderStandalone(tessellator, minecart.getMeta(), minecart.getLightIndex(partialTick));

			GLRenderer.popFrame();
		}
	}
}
