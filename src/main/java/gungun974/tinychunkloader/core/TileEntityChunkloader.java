package gungun974.tinychunkloader.core;

import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;

public class TileEntityChunkloader extends TileEntity {
	@Override
	public void tick() {
		super.tick();
		int currentChunkX = Math.floorDiv(x, 16);
		int currentChunkZ = Math.floorDiv(z, 16);

		// 3x3
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				ChunkLoaderManager.getInstance().keepChunkLoaded(currentChunkX + i, currentChunkZ + j, worldObj);
			}
		}
	}

	@Override
	public boolean canBeCarried(World world, Entity potentialHolder) {
		return true;
	}
}
