package gungun974.tinychunkloader.core;

import net.fabricmc.api.ClientModInitializer;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

import static gungun974.tinychunkloader.core.TinyChunkLoader.MOD_ID;

public class TinyChunkLoaderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TinyChunkLoader.LOGGER.info("Binding to client events...");

		ClientEvents.BLOCK_MODEL_RELOAD.listen(Key.of(MOD_ID), (t) -> new TinyChunkLoaderModels().initBlockModels(t));
		ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of(MOD_ID), (t) -> new TinyChunkLoaderModels().initItemModels(t));
	}
}
