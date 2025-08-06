package gungun974.tinychunkloader.cc.turtle.upgrades;

import com.mojang.nbt.tags.CompoundTag;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.util.BlockPos;
import gungun974.tinychunkloader.core.TinyChunkLoader;
import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import gungun974.tinychunkloader.helpers.UUIDHelper;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class TurtleChunkloader extends AbstractTurtleUpgrade {
	public TurtleChunkloader(int id) {
		super(id, TurtleUpgradeType.PERIPHERAL, TinyChunkLoaderBlocks.CHUNKLOADER);
	}

	@Override
	public IPeripheral createPeripheral(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
		return new TurtleChunkloader.Peripheral(turtle);
	}

	@Override
	public boolean isItemSuitable(@Nonnull ItemStack stack) {
		if (!TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE_CRAFT) {
			return false;
		}
		return super.isItemSuitable(stack);
	}


	@Override
	public void drawTileUpgrade(Tessellator tessellator, TextureManager textureManager, TileTurtle tileEntity, float angle, @NotNull TurtleSide side, float partialTick) {
		boolean success = true;
		ITurtleAccess turtle = tileEntity.getAccess();

		if (turtle != null) {
			CompoundTag turtleNBT = turtle.getUpgradeNBTData(side);
			success = turtleNBT.containsKey("success") && turtleNBT.getBoolean("success");
		}

		if (success) {
			textureManager.loadTexture("/assets/tinychunkloader/textures/block/face.png").bind();
		} else {
			textureManager.loadTexture("/assets/tinychunkloader/textures/block/face_sob.png").bind();
		}
		tessellator.startDrawingQuads();
		if (side == TurtleSide.LEFT) {
			drawUpgradeLeft(tessellator, tileEntity, angle);
		} else {
			drawUpgradeRight(tessellator, tileEntity, angle);
		}
		tessellator.draw();
	}

	@Override
	public void drawItemUpgrade(Tessellator tessellator, TextureManager textureManager, @NotNull TurtleSide side) {
		textureManager.loadTexture("/assets/tinychunkloader/textures/block/face.png").bind();
		tessellator.startDrawingQuads();
		if (side == TurtleSide.LEFT) {
			drawUpgradeLeft(tessellator);
		} else {
			drawUpgradeRight(tessellator);
		}
		tessellator.draw();
	}

	@Override
	public @NotNull String getUnlocalisedAdjective() {
		return "upgrade.computercraft.chunkloader.adjective";
	}

	@Override
	public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
		super.update(turtle, side);

		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof Peripheral) {
			Peripheral chunkyPeripheral = (Peripheral) peripheral;
			chunkyPeripheral.setLocation(turtle.getWorld(), turtle.getPosition());
			chunkyPeripheral.updateChunkState();

			if (turtle.getUpgradeNBTData(side).getBoolean("success") != chunkyPeripheral.success) {
				turtle.getUpgradeNBTData(side)
					.putBoolean("success", chunkyPeripheral.success);
				turtle.updateUpgradeNBTData(side);
			}

		}
	}

	public static class Peripheral implements IPeripheral {
		ITurtleAccess turtle;

		Peripheral(ITurtleAccess turtle) {
			this.turtle = turtle;
		}

		private World world = null;
		private BlockPos position = new BlockPos(0, 0, 0);

		boolean success = true;

		void setLocation(World world, BlockPos position) {
			this.position = position;
			this.world = world;
		}

		public World getWorld() {
			return world;
		}

		public BlockPos getPosition() {
			return world != null ? position : null;
		}

		@Override
		public @NotNull String getType() {
			return "chunkloader";
		}

		public void updateChunkState() {
			UUID owner = turtle.getOwningPlayer();

			if (owner == null) {
				return;
			}

			World world = getWorld();

			boolean totalSuccess = true;

			if (TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE) {
				for (double xo = -1f; xo <= 1f; xo++) {
					for (double zo = -1f; zo <= 1f; zo++) {
						int currentChunkX = (int) Math.floor((getPosition().x + xo) / 16);
						int currentChunkZ = (int) Math.floor((getPosition().z + zo) / 16);

						for (int i = -(TinyChunkLoader.CHUNKLOADER_TURTLE_RANGE - 1); i <= (TinyChunkLoader.CHUNKLOADER_TURTLE_RANGE - 1); i++) {
							for (int j = -(TinyChunkLoader.CHUNKLOADER_TURTLE_RANGE - 1); j <= (TinyChunkLoader.CHUNKLOADER_TURTLE_RANGE - 1); j++) {
								final boolean success = ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + i, currentChunkZ + j, world, owner);

								if (!success) {
									totalSuccess = false;
								}
							}
						}
					}
				}
			} else {
				totalSuccess = false;
			}

			success = totalSuccess;
		}

		@Override
		public boolean equals(IPeripheral other) {
			return this == other || (other instanceof Peripheral && turtle == ((Peripheral) other).turtle);
		}


		@LuaFunction(mainThread = true)
		public final MethodResult status() throws LuaException {
			HashMap<String, Object> data = new HashMap<>(7);
			data.put("activated", success);

			UUID owner = turtle.getOwningPlayer();

			if (owner != null) {
				data.put("uuid", owner.toString());
				data.put("player_name", UUIDHelper.getNameFromUUID(owner));
				data.put("player", ChunkLoaderManager.getInstance().getCurrentPlayerTotalLoads(owner));
			}

			data.put("global", ChunkLoaderManager.getInstance().getCurrentTotalLoads());

			data.put("player_max", TinyChunkLoader.PLAYER_CHUNK_LOAD_LIMIT);
			data.put("global_max", TinyChunkLoader.GLOBAL_CHUNK_LOAD_LIMIT);

			return MethodResult.of(data);
		}
	}
}
