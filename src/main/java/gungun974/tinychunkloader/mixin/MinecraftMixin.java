package gungun974.tinychunkloader.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.chunk.provider.ChunkProviderDynamic;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@SuppressWarnings("unused")
	public IChunkProvider createChunkProvider(World world, IChunkLoader chunkLoader) {
		ChunkProviderDynamic provider = new ChunkProviderDynamic(world, chunkLoader, world.getWorldType().createChunkGenerator(world));
		provider.forceLoadedChunksLimit = 1024;
		return provider;
	}
}
