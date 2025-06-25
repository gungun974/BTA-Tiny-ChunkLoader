package gungun974.tinychunkloader.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.UUID;

public class TileEntityChunkloader extends TileEntity {

	private @Nullable UUID owner;

	@Override
	public void tick() {
		super.tick();

		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		if (owner == null) {
			if (worldObj == null) {
				return;
			}

			final Player player = worldObj.getClosestPlayer(x, y, z, 16);

			if (player == null) {
				return;
			}

			owner = player.uuid;

			return;
		}

		int currentChunkX = Math.floorDiv(x, 16);
		int currentChunkZ = Math.floorDiv(z, 16);

		// 3x3
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + i, currentChunkZ + j, worldObj, owner);
			}
		}
	}

	@Override
	public boolean canBeCarried(World world, Entity potentialHolder) {
		return true;
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		super.readFromNBT(tag);

		this.owner = UUIDHelper.readFromTag(tag, "OwnerUUID");
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		super.writeToNBT(tag);

		UUIDHelper.writeToTag(tag, this.owner, "OwnerUUID");
	}
}
