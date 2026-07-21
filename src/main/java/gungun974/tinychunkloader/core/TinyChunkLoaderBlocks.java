package gungun974.tinychunkloader.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

import static gungun974.tinychunkloader.core.TinyChunkLoader.MOD_ID;

public class TinyChunkLoaderBlocks {
	public static Block<BlockLogic> CHUNKLOADER;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterBlocks() {
		currentGeneratedId = TinyChunkLoader.startBlockID;

		TileEntityDispatcher.addMapping(TileEntityChunkloader.class, new NamespaceID(MOD_ID, "chunkloader"));

		CHUNKLOADER = new BlockBuilder(MOD_ID)
			.setHardness(1.0F)
			.setResistance(2000.0F)
			.setTags(BlockTags.MINEABLE_BY_AXE)
			.setTileEntity(TileEntityChunkloader::new)
			.setBlockSound(BlockSounds.METAL)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build("chunkloader", generateNexId(), b -> new BlockLogicChunkloader(b, Materials.METAL));

	}
}
