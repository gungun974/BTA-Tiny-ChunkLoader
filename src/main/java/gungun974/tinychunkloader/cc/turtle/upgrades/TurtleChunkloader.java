package gungun974.tinychunkloader.cc.turtle.upgrades;

import com.mojang.nbt.tags.CompoundTag;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import gungun974.tinychunkloader.core.TinyChunkLoader;
import gungun974.tinychunkloader.core.TinyChunkLoaderBlocks;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import gungun974.tinychunkloader.helpers.UUIDHelper;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class TurtleChunkloader extends AbstractTurtleUpgrade {
	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockModelGeneric modelLeft;
	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockModelGeneric modelRight;
	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockModelGeneric modelLeftOff;
	@Nullable
	@Environment(EnvType.CLIENT)
	private BlockModelGeneric modelRightOff;

	@Environment(EnvType.CLIENT)
	private void ensureModels() {
		if (modelLeft == null) {
			modelLeft = new BlockModelGeneric(Blocks.STONE, BlockModelDispatcher.loadDataModel("tinychunkloader:item/turtle_chunkloader_upgrade_left").asModel());
			modelRight = new BlockModelGeneric(Blocks.STONE, BlockModelDispatcher.loadDataModel("tinychunkloader:item/turtle_chunkloader_upgrade_right").asModel());
			modelLeftOff = new BlockModelGeneric(Blocks.STONE, BlockModelDispatcher.loadDataModel("tinychunkloader:item/turtle_chunkloader_upgrade_left_off").asModel());
			modelRightOff = new BlockModelGeneric(Blocks.STONE, BlockModelDispatcher.loadDataModel("tinychunkloader:item/turtle_chunkloader_upgrade_right_off").asModel());
		}
	}

	public TurtleChunkloader(int id) {
		super(id, TurtleUpgradeType.PERIPHERAL, TinyChunkLoaderBlocks.CHUNKLOADER);
	}

	@Override
	public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
		return new TurtleChunkloader.Peripheral(turtle);
	}

	@Override
	public boolean isItemSuitable(@NotNull ItemStack stack) {
		if (!TinyChunkLoader.ENABLE_CHUNKLOADER_TURTLE_CRAFT) {
			return false;
		}
		return super.isItemSuitable(stack);
	}


	@Override
	@Environment(EnvType.CLIENT)
	public void drawTileUpgrade(Tessellator tessellator, TextureManager textureManager, TileTurtle tileEntity, float angle, @NotNull TurtleSide side, float partialTick) {
		ensureModels();

		boolean success = true;
		ITurtleAccess turtle = tileEntity.getAccess();

		if (turtle != null) {
			CompoundTag turtleNBT = turtle.getUpgradeNBTData(side);
			success = turtleNBT.containsKey("success") && turtleNBT.getBoolean("success");
		}

		byte lightIndex = tileEntity.worldObj.getLightIndex(tileEntity.tilePos, 0);
		float toolAngle = tileEntity.getToolRenderAngle(side, partialTick);

		BlockModelGeneric model;
		if (side == TurtleSide.LEFT) {
			model = success ? modelLeft : modelLeftOff;
		} else {
			model = success ? modelRight : modelRightOff;
		}

		TextureRegistry.worldAtlas.bind();
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().rotateX((float) Math.toRadians(-toolAngle));
		model.renderStandalone((TessellatorGeneral) tessellator, 0, lightIndex);
		GLRenderer.popFrame();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void drawItemUpgrade(TessellatorGeneral tessellator, byte lightIndex, @NotNull TurtleSide side) {
		ensureModels();

		BlockModelGeneric model = side == TurtleSide.LEFT ? modelLeft : modelRight;

		TextureRegistry.worldAtlas.bind();
		GLRenderer.pushFrame();
		model.renderStandalone(tessellator, 0, lightIndex);
		GLRenderer.popFrame();
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
		private TilePosc position = new TilePos(0, 0, 0);

		boolean success = true;

		void setLocation(World world, TilePosc position) {
			this.position = position;
			this.world = world;
		}

		public World getWorld() {
			return world;
		}

		public TilePosc getPosition() {
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
						int currentChunkX = (int) Math.floor((getPosition().x() + xo) / 16);
						int currentChunkZ = (int) Math.floor((getPosition().z() + zo) / 16);

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
