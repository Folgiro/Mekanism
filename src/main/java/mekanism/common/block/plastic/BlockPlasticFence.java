package mekanism.common.block.plastic;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.block.interfaces.IBlockOreDict;
import mekanism.common.block.interfaces.IColoredBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class BlockPlasticFence extends FenceBlock implements IColoredBlock, IBlockOreDict {

    private final EnumColor color;

    public BlockPlasticFence(EnumColor color) {
        super(Material.CLAY, Material.CLAY.getMaterialMapColor());
        this.color = color;
        setRegistryName(new ResourceLocation(Mekanism.MODID, color.registry_prefix + "_plastic_fence"));
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    @Override
    public List<String> getOredictEntries() {
        List<String> entries = new ArrayList<>();
        entries.add("fencePlastic");
        if (color.dyeName != null) {
            //As of the moment none of the colors used have a null dye name but if the other ones get used this is needed
            entries.add("fencePlastic" + color.dyeName);
        }
        return entries;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH);
    }

    public static class PlasticFenceStateMapper extends StateMapperBase {

        @Nonnull
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull BlockState state) {
            String properties = "east=" + state.get(EAST) + ",";
            properties += "north=" + state.get(NORTH) + ",";
            properties += "south=" + state.get(SOUTH) + ",";
            properties += "west=" + state.get(WEST);
            ResourceLocation baseLocation = new ResourceLocation(Mekanism.MODID, "plastic_fence");
            return new ModelResourceLocation(baseLocation, properties);
        }
    }
}