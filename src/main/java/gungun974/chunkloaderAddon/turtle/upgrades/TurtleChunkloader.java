package gungun974.chunkloaderAddon.turtle.upgrades;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.util.BlockPos;
import dan200.computercraft.shared.util.PortableTickScheduler;
import gungun974.chunkloaderAddon.ChunkLoaderManager;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.world.chunk.provider.ChunkProviderDynamic;
import net.minecraft.core.Global;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class TurtleChunkloader extends AbstractTurtleUpgrade {
	public TurtleChunkloader(int id) {
		super(id, TurtleUpgradeType.PERIPHERAL, Blocks.BLOCK_DIAMOND);
	}

	@Override
	public IPeripheral createPeripheral(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
		return new TurtleChunkloader.Peripheral(turtle);
	}

	@Override
	public void drawTileUpgrade(Tessellator tessellator, TextureManager textureManager, TileTurtle tileEntity, float angle, @NotNull TurtleSide side, float partialTick) {
		textureManager.loadTexture("/assets/minecraft/textures/block/block_diamond/side_retro.png").bind();
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
		textureManager.loadTexture("/assets/minecraft/textures/block/block_diamond/side_retro.png").bind();
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
		return "upgrade.computercraft.teleport.adjective";
	}

	@Override
	public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
		super.update(turtle, side);

		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof Peripheral) {
			Peripheral chunkyPeripheral = (Peripheral) peripheral;
			chunkyPeripheral.setLocation(turtle.getWorld(), turtle.getPosition());
			chunkyPeripheral.updateChunkState();
		}
	}

	public static class Peripheral implements IPeripheral {
		ITurtleAccess turtle;

		Peripheral(ITurtleAccess turtle) {
			this.turtle = turtle;
		}

		private World world = null;
		private BlockPos position = new BlockPos(0, 0, 0);

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
			World world = getWorld();
			int currentChunkX = Math.floorDiv(getPosition().x, 16);
			int currentChunkZ = Math.floorDiv(getPosition().z, 16);

			ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX, currentChunkZ, world);
			ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + 1, currentChunkZ, world);
			ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX - 1, currentChunkZ, world);
			ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX, currentChunkZ + 1, world);
			ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX, currentChunkZ - 1, world);
		}

		@Override
		public boolean equals(IPeripheral other) {
			return this == other || (other instanceof Peripheral && turtle == ((Peripheral) other).turtle);
		}
	}
}
