package gungun974.tinychunkloader.core;

import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Sides;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;

public class BlockModelChunkloader<T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelChunkloader(Block<T> block) {
		super(block);
	}

	@Override
	public IconCoordinate getBlockTexture(WorldSource blockAccess, TilePosc pos, Side side) {
		int currentMetadata = blockAccess.getBlockData(pos);
		int index = Sides.orientationLookUpHorizontal[side.id];
		if (currentMetadata == 1 && (side == Side.TOP || side == Side.SOUTH)) {
			IconCoordinate original = this.blockTextures.get(side);

			assert original != null;

			return TextureRegistry.getTexture(original.namespaceId.namespace() + ":block/" + original.namespaceId.value() + "_sob");
		} else {
			return this.blockTextures.get(Side.fromId(index));
		}
	}

	public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int currentMetadata) {
		int index = Sides.orientationLookUpHorizontal[side.id];
		if (currentMetadata == 1 && (side == Side.TOP || side == Side.SOUTH)) {
			IconCoordinate original = this.blockTextures.get(side);

			assert original != null;

			return TextureRegistry.getTexture(original.namespaceId.namespace() + ":block/" + original.namespaceId.value() + "_sob");
		} else {
			return this.blockTextures.get(Side.fromId(index));
		}
	}

}
