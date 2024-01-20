package com.onlyex.naxtech.common.metatileentities.multi.hpca;

import gregtech.api.GTValues;
import gregtech.api.capability.IHPCAComputationProvider;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.hpca.MetaTileEntityHPCAComponent;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityHPCAAdvancedComputation extends MetaTileEntityHPCAComponent implements IHPCAComputationProvider {

    private final boolean ultimate;

    public MetaTileEntityHPCAAdvancedComputation(ResourceLocation metaTileEntityId, boolean ultimate) {
        super(metaTileEntityId);
        this.ultimate = ultimate;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHPCAAdvancedComputation(metaTileEntityId, ultimate);
    }

    @Override
    public boolean isAdvanced() {
        return ultimate;
    }

    @Override//正面覆盖 T/F TODO
    public SimpleOverlayRenderer getFrontOverlay() {
        return ultimate ?
                Textures.HPCA_ADVANCED_COMPUTATION_OVERLAY :
                Textures.HPCA_COMPUTATION_OVERLAY;
    }

    @Override//组件图标(UI) T/F TODO
    public TextureArea getComponentIcon() {
        return ultimate ?
                GuiTextures.HPCA_ICON_ADVANCED_COMPUTATION_COMPONENT :
                GuiTextures.HPCA_ICON_COMPUTATION_COMPONENT;
    }

    @Override//运转中正面覆盖 T/F TODO
    public SimpleOverlayRenderer getFrontActiveOverlay() {
        return ultimate ?
                Textures.HPCA_ADVANCED_COMPUTATION_ACTIVE_OVERLAY :
                Textures.HPCA_COMPUTATION_ACTIVE_OVERLAY;
    }

    @Override//维持(EU/t) T/F
    public int getUpkeepEUt() {
        return GTValues.VA[ultimate ? GTValues.UHV: GTValues.UV ];
    }

    @Override//最大(EU/t) T/F
    public int getMaxEUt() {
        return GTValues.VA[ultimate ? GTValues.UIV : GTValues.UEV ];
    }

    @Override//算力 T/F
    public int getCWUPerTick() {
        return ultimate ? 256 : 64;
    }

    @Override//冷却 T/F
    public int getCoolingPerTick() {
        return ultimate ? 64 : 16;
    }

    @Override
    public boolean canBeDamaged() {
        return true;
    }

}
