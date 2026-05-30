package wile.rsgauges.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "rsgauges", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Automatically add ALL rsgauges blocks to the mineable/pickaxe tag
        Iterable<Block> blocks = BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("rsgauges")).toList();
        for (Block block : blocks) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        }
    }
}
