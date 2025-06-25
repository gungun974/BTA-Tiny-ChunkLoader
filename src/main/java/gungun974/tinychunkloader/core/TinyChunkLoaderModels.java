package gungun974.tinychunkloader.core;

import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

public class TinyChunkLoaderModels implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		final IconCoordinate a = TextureRegistry.getTexture("tinychunkloader:block/side");

		try {
			TextureRegistry.initializeAllFiles("tinychunkloader", a.parentAtlas, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		ModelHelper.setBlockModel(TinyChunkLoaderBlocks.CHUNKLOADER, () -> new BlockModelChunkloader<>(TinyChunkLoaderBlocks.CHUNKLOADER)
			.setAllTextures(0, "tinychunkloader:block/side")
			.setTex(0, "tinychunkloader:block/face", Side.TOP)
		);

		TinyChunkLoader.LOGGER.info("Block Models initialized.");
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {

	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {
	}
}

