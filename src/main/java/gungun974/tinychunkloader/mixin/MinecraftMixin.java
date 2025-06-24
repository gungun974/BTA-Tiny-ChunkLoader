package gungun974.tinychunkloader.mixin;

import gungun974.tinychunkloader.core.ChunkProviderDynamic2;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	/**
	 * @author gungun974
	 * @reason Use a ChunkProviderDynamic2
	 */
	@Overwrite
	@SuppressWarnings("unused")
	public IChunkProvider createChunkProvider(World world, IChunkLoader chunkLoader) {
		ChunkProviderDynamic2 provider = new ChunkProviderDynamic2(world, chunkLoader, world.getWorldType().createChunkGenerator(world));
		provider.forceLoadedChunksLimit = 1024;
		return provider;
	}
}
