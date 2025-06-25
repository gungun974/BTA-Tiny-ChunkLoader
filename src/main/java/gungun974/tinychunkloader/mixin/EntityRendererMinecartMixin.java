package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.entity.EntityRendererMinecart;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.Global;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRendererMinecart.class, remap = false)
public class EntityRendererMinecartMixin {
	@Inject(
		method = "render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/vehicle/EntityMinecart;DDDFF)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/vehicle/EntityMinecart;getType()B", ordinal = 1)
	)
	public void displayCupboard(Tessellator tessellator, EntityMinecart minecart, double x, double y, double z, float yaw, float partialTick, CallbackInfo ci) {
		if (minecart.getType() == 43) {
			float brightness = 1.0F;
			if (LightmapHelper.isLightmapEnabled()) {
				LightmapHelper.setLightmapCoord(minecart.getLightmapCoord(partialTick));
			} else if (!Global.accessor.isFullbrightEnabled()) {
				brightness = minecart.getBrightness(partialTick);
			}

			((BlockModel) BlockModelDispatcher.getInstance().getDispatch(TinyChunkLoaderBlocks.CHUNKLOADER)).renderBlockOnInventory(tessellator, minecart.getMeta(), brightness, (Integer) null);
		}
	}

}

