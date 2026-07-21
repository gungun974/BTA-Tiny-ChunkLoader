package gungun974.tinychunkloader.core;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.helper.Side;

public class TinyChunkLoaderModels {

	public void initBlockModels(BlockModelDispatcher dispatcher) {
		final IconCoordinate a = TextureRegistry.getTexture("tinychunkloader:block/side");

		try {
			TextureRegistry.initializeAllFiles("tinychunkloader", a.parentAtlas, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		BlockModelDispatcher.getInstance().addDispatch(TinyChunkLoaderBlocks.CHUNKLOADER, new BlockModelChunkloader<>(TinyChunkLoaderBlocks.CHUNKLOADER)
			.setAllTextures("tinychunkloader:block/side")
			.setTex("tinychunkloader:block/face", Side.TOP)
		);

		TinyChunkLoader.LOGGER.info("Block Models initialized.");
	}

	public void initItemModels(ItemModelDispatcher dispatcher) {

	}
}
