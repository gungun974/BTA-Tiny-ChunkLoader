package gungun974.tinychunkloader.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicChunkloader extends BlockLogic {
	public BlockLogicChunkloader(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xPlaced, double yPlaced) {
		return ((TileEntityChunkloader) world.getTileEntity(tilePos)).onBlockRightClicked(player, side, xPlaced, yPlaced);
	}
}
