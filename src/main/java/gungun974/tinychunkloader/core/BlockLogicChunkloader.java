package gungun974.tinychunkloader.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicChunkloader extends BlockLogic {
	public BlockLogicChunkloader(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
		return ((TileEntityChunkloader) world.getTileEntity(x, y, z)).onBlockRightClicked(player, side, xPlaced, yPlaced);
	}
}
