package gungun974.tinychunkloader.mixin;

import net.minecraft.client.option.GameSettings;
import net.minecraft.client.world.WorldClient;
import net.minecraft.client.world.chunk.provider.ChunkProviderDynamic;
import net.minecraft.client.world.chunk.provider.ChunkProviderStatic;
import net.minecraft.client.world.chunk.provider.ChunkProviderThreadedClient;
import net.minecraft.client.world.chunk.provider.SelectedProvider;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkLoader;
import net.minecraft.core.world.chunk.provider.ChunkProvider;
import net.minecraft.core.world.save.DimensionData;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.settings.WorldConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldClient.class, remap = false)
public abstract class WorldClientMixin extends World {
	protected WorldClientMixin(@NotNull Dimension dimension, @NotNull LevelStorage levelStorage, @Nullable WorldConfiguration worldConfiguration, @Nullable LevelData levelData, @Nullable DimensionData dimensionData) {
		super(dimension, levelStorage, worldConfiguration, levelData, dimensionData);
	}

	/**
	 * @author gungun974
	 * @reason force the dynamic chunk provider
	 */
	@Overwrite
	protected @NotNull ChunkProvider createChunkProvider(@NotNull LevelStorage levelStorage) {
		ChunkLoader chunkLoader = levelStorage.getChunkLoader(this.dimension);
		ChunkProviderDynamic provider = new ChunkProviderDynamic(this, chunkLoader, this.getWorldType().createChunkGenerator(this));
		provider.forceLoadedChunksLimit = 1024;
		return provider;
	}

}
