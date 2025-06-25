package gungun974.tinychunkloader.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.UUID;

public class TileEntityChunkloader extends TileEntity {

	private @Nullable UUID owner;

	private boolean success = true;

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

		boolean totalSuccess = true;

		// 3x3
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				final boolean success = ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + i, currentChunkZ + j, worldObj, owner);

				if (!success) {
					totalSuccess = false;
				}
			}
		}

		this.success = totalSuccess;

		if (worldObj != null) {
			if (!totalSuccess) {
				if (getBlockMeta() != 1) {
					worldObj.setBlockMetadataWithNotify(x, y, z, 1);
				}
			} else {
				if (getBlockMeta() != 0) {
					worldObj.setBlockMetadataWithNotify(x, y, z, 0);
				}
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

	public boolean onBlockRightClicked(Player player, Side side, double xPlaced, double yPlaced) {
		I18n i18n = I18n.getInstance();

		player.sendMessage(
			i18n.translateKeyAndFormat("chat.tinychunkloader.chunkloader_status.status",
				success ? TextFormatting.formatted(i18n.translateKey("general.tinychunkloader.activated"), TextFormatting.GREEN) :
					TextFormatting.formatted(i18n.translateKey("general.tinychunkloader.deactivated"), TextFormatting.RED)
			)
		);

		TextFormatting color = TextFormatting.GREEN;

		if (owner != null) {
			if (ChunkLoaderManager.getInstance().getCurrentPlayerTotalLoads(owner) > TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT) {
				color = TextFormatting.RED;
			}

			player.sendMessage(
				i18n.translateKeyAndFormat("chat.tinychunkloader.chunkloader_status.player_loaded",
					gungun974.tinychunkloader.helpers.UUIDHelper.getNameFromUUID(owner),
					TextFormatting.formatted(String.valueOf(ChunkLoaderManager.getInstance().getCurrentPlayerTotalLoads(owner)), color),
					TextFormatting.formatted(String.valueOf(TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT), color)
				)
			);
		}

		color = TextFormatting.GREEN;

		if (ChunkLoaderManager.getInstance().getCurrentTotalLoads() > TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT) {
			color = TextFormatting.RED;
		}

		player.sendMessage(
			i18n.translateKeyAndFormat("chat.tinychunkloader.chunkloader_status.total_loaded",
				TextFormatting.formatted(String.valueOf(ChunkLoaderManager.getInstance().getCurrentTotalLoads()), color),
				TextFormatting.formatted(String.valueOf(TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT), color)
			)
		);


		return false;
	}
}
