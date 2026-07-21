package gungun974.tinychunkloader.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = LevelData.class, remap = false)
public class LevelDataMixin {
	@Inject(method = "serialize", at = @At("TAIL"))
	private static void serializeTinyChunkloader(LevelData levelData, CompoundTag out, CallbackInfoReturnable<CompoundTag> cir) {
		CompoundTag tinyChunkLoaderTag = new CompoundTag();
		ListTag dimensions = new ListTag();

		Map<Dimension, Map<ChunkCoordinate, Integer>> dimensionsToLoads = ChunkLoaderManager.getInstance().getDimensionToLoads();

		for (Map.Entry<Dimension, Map<ChunkCoordinate, Integer>> dimensionMapEntry : dimensionsToLoads.entrySet()) {
			CompoundTag dimensionTag = new CompoundTag();
			dimensionTag.putInt("id", dimensionMapEntry.getKey().id);

			ListTag chunksToLoads = new ListTag();

			for (Map.Entry<ChunkCoordinate, Integer> chunkEntry : dimensionMapEntry.getValue().entrySet()) {
				ChunkCoordinate coordinate = chunkEntry.getKey();
				int currentPing = chunkEntry.getValue();

				CompoundTag tag = new CompoundTag();

				tag.putInt("x", coordinate.x);
				tag.putInt("z", coordinate.z);
				tag.putByte("p", (byte) currentPing);

				chunksToLoads.addTag(tag);
			}

			dimensionTag.putList("chunks", chunksToLoads);
			dimensions.addTag(dimensionTag);
		}

		tinyChunkLoaderTag.putList("dimensions", dimensions);
		out.putCompound("TinyChunkLoader", tinyChunkLoaderTag);
	}

	@Inject(method = "deserialize", at = @At("TAIL"))
	private static void deserializeTinyChunkloader(CompoundTag tag, CallbackInfoReturnable<LevelData> cir) {
		Map<Dimension, Map<ChunkCoordinate, Integer>> localDimensionsToLoads = new HashMap<>();

		if (tag.containsKey("TinyChunkLoader")) {
			CompoundTag tinyChunkLoaderTag = tag.getCompound("TinyChunkLoader");
			ListTag dimensions = tinyChunkLoaderTag.getList("dimensions");

			for (int i = 0; i < dimensions.tagCount(); i++) {
				CompoundTag dimensionTag = (CompoundTag) dimensions.tagAt(i);

				int dimensionID = dimensionTag.getInteger("id");

				Dimension dimension = Dimension.getDimensionList().get(dimensionID);

				if (dimension == null) {
					continue;
				}

				Map<ChunkCoordinate, Integer> localChunkToLoads = new HashMap<>();

				ListTag chunksToLoads = dimensionTag.getList("chunks");

				for (int j = 0; j < chunksToLoads.tagCount(); j++) {
					CompoundTag chunkTag = (CompoundTag) chunksToLoads.tagAt(j);
					int chunkX = chunkTag.getInteger("x");
					int chunkZ = chunkTag.getInteger("z");
					int currentPing = chunkTag.getByte("p") & 0xff;

					localChunkToLoads.put(new ChunkCoordinate(chunkX, chunkZ), currentPing);
				}

				localDimensionsToLoads.put(dimension, localChunkToLoads);
			}
		}

		ChunkLoaderManager.getInstance().setDimensionToLoads(localDimensionsToLoads);
	}
}
