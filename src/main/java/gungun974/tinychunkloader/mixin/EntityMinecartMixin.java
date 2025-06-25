package gungun974.tinychunkloader.mixin;

import com.mojang.nbt.tags.*;
import gungun974.tinychunkloader.core.TileEntityChunkloader;
import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.UUID;

@Mixin(value = EntityMinecart.class, remap = false)
public abstract class EntityMinecartMixin extends Entity {

	public EntityMinecartMixin(@Nullable World world) {
		super(world);
	}

	@Shadow
	public abstract byte getType();

	@Shadow
	public abstract void setType(byte type);

	@Shadow
	public abstract void setMeta(int meta);

	@Shadow
	public abstract int getMeta();

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/vehicle/EntityMinecart;getType()B"))
	public void dropCupboard(Entity entity, int baseDamage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		if (this.getType() == 43) {
			this.dropItem(TinyChunkLoaderBlocks.CHUNKLOADER.id(), 1, 0.0F);
		}
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void interactWithCupboard(Player player, CallbackInfoReturnable<Boolean> cir) {
		switch (this.getType()) {
			case 0:
				if (!this.world.isClientSide && this.passenger == null && player.isSneaking() && player.getHeldObject() instanceof CarriedBlock) {
					CarriedBlock carriedBlock = (CarriedBlock) player.getHeldObject();
					if (carriedBlock.entity instanceof TileEntityChunkloader) {
						this.setType((byte) 43);
						this.setMeta(0);

						player.setHeldObject((ICarriable) null);
						cir.setReturnValue(true);
						cir.cancel();
						return;
					}
				}
				break;
			case 43:
				if (!this.world.isClientSide) {
					if (player.isSneaking() && player.inventory.getCurrentItem() == null && player.getHeldObject() == null) {
						TileEntityChunkloader tileEntityChunkloader = new TileEntityChunkloader();

						tileEntityChunkloader.worldObj = null;
						tileEntityChunkloader.carriedBlock = tileEntityChunkloader.getCarriedEntry(this.world, player, TinyChunkLoaderBlocks.CHUNKLOADER, 0);
						player.setHeldObject(tileEntityChunkloader.carriedBlock);
						this.setType((byte) 0);
						this.setMeta(0);
					} else {
						TileEntityChunkloader.showChunkloaderInfo(player, success, owner);
					}

					cir.setReturnValue(true);
					cir.cancel();
				}
		}
	}

	@Unique
	private @Nullable UUID owner;

	@Unique
	private boolean success = true;

	@Inject(method = "tick", at = @At("TAIL"))
	public void handleChunkLoading(CallbackInfo ci) {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		if (getType() != 43) {
			return;
		}

		if (owner == null) {
			if (world == null) {
				return;
			}

			final Player player = world.getClosestPlayer(x, y, z, 16);

			if (player == null) {
				return;
			}

			owner = player.uuid;

			return;
		}

		int currentChunkX = (int) Math.floor(x / 16);
		int currentChunkZ = (int) Math.floor(z / 16);

		boolean totalSuccess = true;

		// 3x3
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				final boolean success = ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + i, currentChunkZ + j, world, owner);

				if (!success) {
					totalSuccess = false;
				}
			}
		}

		this.success = totalSuccess;

		if (world != null) {
			if (!totalSuccess) {
				if (getMeta() != 1) {
					setMeta(1);
				}
			} else {
				if (getMeta() != 0) {
					setMeta(0);
				}
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		UUIDHelper.writeToTag(tag, this.owner, "TinyChunkloaderOwnerUUID");
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		this.owner = UUIDHelper.readFromTag(tag, "TinyChunkloaderOwnerUUID");
	}
}
