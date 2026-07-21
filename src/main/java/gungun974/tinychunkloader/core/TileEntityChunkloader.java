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

		if (EnvironmentHelper.isMultiplayerClient()) {
			return;
		}

		if (owner == null) {
			if (worldObj == null) {
				return;
			}

			final Player player = worldObj.getClosestPlayer(tilePos.x, tilePos.y, tilePos.z, 16);

			if (player == null) {
				return;
			}

			owner = player.uuid;

			return;
		}

		int currentChunkX = Math.floorDiv(tilePos.x, 16);
		int currentChunkZ = Math.floorDiv(tilePos.z, 16);

		boolean totalSuccess = true;

		if (TinyChunkLoader.ENABLE_CHUNKLOADER_BLOCK) {
			for (int i = -(TinyChunkLoader.CHUNKLOADER_BLOCK_RANGE - 1); i <= (TinyChunkLoader.CHUNKLOADER_BLOCK_RANGE - 1); i++) {
				for (int j = -(TinyChunkLoader.CHUNKLOADER_BLOCK_RANGE - 1); j <= (TinyChunkLoader.CHUNKLOADER_BLOCK_RANGE - 1); j++) {

					final boolean success = ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + i, currentChunkZ + j, worldObj, owner);

					if (!success) {
						totalSuccess = false;
					}
				}
			}
		} else {
			totalSuccess = false;
		}

		this.success = totalSuccess;

		if (worldObj != null) {
			if (!totalSuccess) {
				if (getBlockMeta() != 1) {
					worldObj.setBlockDataNotify(tilePos, 1);
				}
			} else {
				if (getBlockMeta() != 0) {
					worldObj.setBlockDataNotify(tilePos, 0);
				}
			}
		}
	}

	@Override
	public void readAdditionalData(CompoundTag tag) {
		this.owner = UUIDHelper.readFromTag(tag, "OwnerUUID");
	}

	@Override
	public void writeAdditionalData(CompoundTag tag) {
		UUIDHelper.writeToTag(tag, this.owner, "OwnerUUID");
	}

	public boolean onBlockRightClicked(Player player, Side side, double xPlaced, double yPlaced) {
		if (EnvironmentHelper.isMultiplayerClient()) {
			return false;
		}
		showChunkloaderInfo(player, success, owner);
		return false;
	}

	public static void showChunkloaderInfo(Player player, boolean success, UUID owner) {
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
	}
}
