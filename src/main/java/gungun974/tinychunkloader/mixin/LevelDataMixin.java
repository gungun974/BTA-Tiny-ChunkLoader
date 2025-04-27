package gungun974.tinychunkloader.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import gungun974.tinychunkloader.RememberChunkToLoad;
import gungun974.tinychunkloader.helpers.ChunkLoaderManager;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = LevelData.class, remap = false)
public class LevelDataMixin implements RememberChunkToLoad {
	@Inject(method = "updateTagCompound", at = @At("TAIL"))
	private void updateTagCompoundWithTinyChunkloader(CompoundTag levelTag, CompoundTag playerTag, CallbackInfo ci) {
		CompoundTag tinyChunkLoaderTag = new CompoundTag();

		Map<Dimension, Map<ChunkCoordinate, Integer>> dimensionsToLoads = ChunkLoaderManager.getInstance().getDimensionToLoads();

		for (Map.Entry<Dimension, Map<ChunkCoordinate, Integer>> dimensionMapEntry : dimensionsToLoads.entrySet()) {
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


			tinyChunkLoaderTag.putList(String.valueOf(dimensionMapEntry.getKey().id), chunksToLoads);
		}

		levelTag.putCompound("TinyChunkLoader", tinyChunkLoaderTag);
	}

	@Unique
	private Map<Dimension, Map<ChunkCoordinate, Integer>> localDimensionsToLoads = new HashMap<>();

	@Inject(method = "readFromCompoundTag", at = @At("TAIL"))
	private void readFromCompoundTagWithTinyChunkloader(CompoundTag levelTag, CallbackInfo ci) {
		CompoundTag tinyChunkLoaderTag = levelTag.getCompound("TinyChunkLoader");

		localDimensionsToLoads.clear();

		for (Map.Entry<String, Tag<?>> entry : tinyChunkLoaderTag.getValue().entrySet()) {
			final Tag<?> rawTag = entry.getValue();
			if (!(rawTag instanceof ListTag)) {
				continue;
			}

			int dimensionID = Integer.parseInt(entry.getKey());

			Dimension dimension = Dimension.getDimensionList().get(dimensionID);

			Map<ChunkCoordinate, Integer> localChunkToLoads = localDimensionsToLoads.getOrDefault(dimension, new HashMap<>());

			final ListTag chunksToLoads = ((ListTag) rawTag);

			for (int i = 0; i < chunksToLoads.tagCount(); i++) {
				CompoundTag tag = (CompoundTag) chunksToLoads.tagAt(i);
				int chunkX = tag.getInteger("x");
				int chunkZ = tag.getInteger("z");
				int currentPing = tag.getByte("p") & 0xff;

				localChunkToLoads.put(new ChunkCoordinate(chunkX, chunkZ), currentPing);
			}

			localDimensionsToLoads.put(dimension, localChunkToLoads);
		}
	}

	public Map<Dimension, Map<ChunkCoordinate, Integer>> tinyChunkLoader$getLocalDimensionsToLoads() {
		return localDimensionsToLoads;
	}
}

