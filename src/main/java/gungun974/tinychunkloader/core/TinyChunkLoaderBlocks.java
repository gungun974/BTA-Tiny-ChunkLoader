package gungun974.tinychunkloader.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;

import static gungun974.tinychunkloader.core.TinyChunkLoader.MOD_ID;

public class TinyChunkLoaderBlocks {
	public static Block<BlockLogic> CHUNKLOADER;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterBlocks() {
		currentGeneratedId = TinyChunkLoader.startBlockID;

		EntityHelper.createTileEntity(TileEntityChunkloader.class, NamespaceID.getPermanent(MOD_ID, "chunkloader"));

		CHUNKLOADER = new BlockBuilder(MOD_ID)
			.setHardness(1.0F)
			.setResistance(2000.0F)
			.setTags(BlockTags.MINEABLE_BY_AXE)
			.setImmovable()
			.setTileEntity(TileEntityChunkloader::new)
			.setBlockSound(BlockSounds.METAL)
			.build("chunkloader", generateNexId(), b -> new BlockLogic(b, Material.metal));

	}
}
