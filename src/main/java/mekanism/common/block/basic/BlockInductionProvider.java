package mekanism.common.block.basic;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockTileDrops;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.interfaces.ITieredBlock;
import mekanism.common.tier.InductionProviderTier;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.induction_provider.TileEntityAdvancedInductionProvider;
import mekanism.common.tile.induction_provider.TileEntityBasicInductionProvider;
import mekanism.common.tile.induction_provider.TileEntityEliteInductionProvider;
import mekanism.common.tile.induction_provider.TileEntityInductionProvider;
import mekanism.common.tile.induction_provider.TileEntityUltimateInductionProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockInductionProvider extends BlockTileDrops implements ITieredBlock<InductionProviderTier>, IHasTileEntity<TileEntityInductionProvider> {

    private final InductionProviderTier tier;

    public BlockInductionProvider(InductionProviderTier tier) {
        super(Material.IRON);
        this.tier = tier;
        setHardness(5F);
        setResistance(10F);
        setRegistryName(new ResourceLocation(Mekanism.MODID, tier.getBaseTier().getSimpleName().toLowerCase(Locale.ROOT) + "_induction_provider"));
    }

    @Override
    public InductionProviderTier getTier() {
        return tier;
    }

    @Override
    @Deprecated
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
        if (!world.isRemote) {
            TileEntity tileEntity = new Coord4D(pos, world).getTileEntity(world);
            if (tileEntity instanceof TileEntityMekanism) {
                ((TileEntityMekanism) tileEntity).onNeighborChange(neighborBlock);
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        switch (tier) {
            case BASIC:
                return new TileEntityBasicInductionProvider();
            case ADVANCED:
                return new TileEntityAdvancedInductionProvider();
            case ELITE:
                return new TileEntityEliteInductionProvider();
            case ULTIMATE:
                return new TileEntityUltimateInductionProvider();
        }
        return null;
    }

    @Nullable
    @Override
    public Class<? extends TileEntityInductionProvider> getTileClass() {
        switch (tier) {
            case BASIC:
                return TileEntityBasicInductionProvider.class;
            case ADVANCED:
                return TileEntityAdvancedInductionProvider.class;
            case ELITE:
                return TileEntityEliteInductionProvider.class;
            case ULTIMATE:
                return TileEntityUltimateInductionProvider.class;
        }
        return null;
    }
}