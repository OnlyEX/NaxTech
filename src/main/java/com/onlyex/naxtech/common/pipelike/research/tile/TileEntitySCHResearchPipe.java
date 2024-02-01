package com.onlyex.naxtech.common.pipelike.research.tile;

import com.onlyex.naxtech.api.capability.NTDataCodes;
import com.onlyex.naxtech.api.capability.NTTileCapabilities;
import com.onlyex.naxtech.api.capability.hatch.research.suprachronal.ISCHResearchComputationProvider;
import com.onlyex.naxtech.common.pipelike.research.ResearchPipeProperties;
import com.onlyex.naxtech.common.pipelike.research.ResearchPipeType;
import com.onlyex.naxtech.common.pipelike.research.net.ResearchPipeNet;
import com.onlyex.naxtech.common.pipelike.research.net.WorldResearchPipeNet;
import com.onlyex.naxtech.common.pipelike.research.net.handler.SCHResearchNetHandler;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.pipenet.tile.TileEntityPipeBase;
import gregtech.api.util.TaskScheduler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.EnumMap;

public class TileEntitySCHResearchPipe extends TileEntityPipeBase<ResearchPipeType, ResearchPipeProperties> {

    private final EnumMap<EnumFacing, SCHResearchNetHandler> handlers = new EnumMap<>(EnumFacing.class);
    // the OpticalNetHandler can only be created on the server, so we have an empty placeholder for the client
    private final ISCHResearchComputationProvider clientComputationHandler = new DefaultComputationHandler();
    private WeakReference<ResearchPipeNet> currentPipeNet = new WeakReference<>(null);
    private SCHResearchNetHandler defaultHandler;
    private int ticksActive = 0;
    private boolean isActive;

    @Override
    public Class<ResearchPipeType> getPipeTypeClass() {
        return ResearchPipeType.class;
    }

    @Override
    public boolean supportsTicking() {
        return false;
    }

    @Override
    public boolean canHaveBlockedFaces() {
        return false;
    }

    private void initHandlers() {
        ResearchPipeNet net = getResearchPipeNet();
        if (net == null) return;
        for (EnumFacing facing : EnumFacing.VALUES) {
            handlers.put(facing, new SCHResearchNetHandler(net, this, facing));
        }
        defaultHandler = new SCHResearchNetHandler(net, this, null);
    }

    @Nullable
    @Override
    public <T> T getCapabilityInternal(Capability<T> capability, @Nullable EnumFacing facing) {

        if (capability == NTTileCapabilities.SCH_CABABILITY_RESEARCH_PROVIDER) {
            if (world.isRemote) {
                return NTTileCapabilities.SCH_CABABILITY_RESEARCH_PROVIDER.cast(clientComputationHandler);
            }

            if (handlers.isEmpty()) initHandlers();

            checkNetwork();
            return NTTileCapabilities.SCH_CABABILITY_RESEARCH_PROVIDER
                    .cast(handlers.getOrDefault(facing, defaultHandler));
        }
        return super.getCapabilityInternal(capability, facing);
    }

    public void checkNetwork() {
        if (defaultHandler != null) {
            ResearchPipeNet current = getResearchPipeNet();
            if (defaultHandler.getNet() != current) {
                defaultHandler.updateNetwork(current);
                for (SCHResearchNetHandler handler : handlers.values()) {
                    handler.updateNetwork(current);
                }
            }
        }
    }

    public ResearchPipeNet getResearchPipeNet() {
        if (world == null || world.isRemote)
            return null;
        ResearchPipeNet currentPipeNet = this.currentPipeNet.get();
        if (currentPipeNet != null && currentPipeNet.isValid() && currentPipeNet.containsNode(getPipePos()))
            return currentPipeNet; // if current net is valid and does contain position, return it
        WorldResearchPipeNet worldNet = (WorldResearchPipeNet) getPipeBlock().getWorldPipeNet(getPipeWorld());
        currentPipeNet = worldNet.getNetFromPos(getPipePos());
        if (currentPipeNet != null) {
            this.currentPipeNet = new WeakReference<>(currentPipeNet);
        }
        return currentPipeNet;
    }

    @Override
    public void transferDataFrom(IPipeTile<ResearchPipeType, ResearchPipeProperties> tileEntity) {
        super.transferDataFrom(tileEntity);
        if (getResearchPipeNet() == null)
            return;
        TileEntitySCHResearchPipe pipe = (TileEntitySCHResearchPipe) tileEntity;
        if (!pipe.handlers.isEmpty() && pipe.defaultHandler != null) {
            // take handlers from old pipe
            handlers.clear();
            handlers.putAll(pipe.handlers);
            defaultHandler = pipe.defaultHandler;
            checkNetwork();
        } else {
            // create new handlers
            initHandlers();
        }
    }

    @Override
    public void setConnection(EnumFacing side, boolean connected, boolean fromNeighbor) {
        if (!getWorld().isRemote && connected && !fromNeighbor) {
            // never allow more than two connections total
            if (getNumConnections() >= 2) return;

            // also check the other pipe
            TileEntity tile = getWorld().getTileEntity(getPos().offset(side));
            if (tile instanceof IPipeTile<?, ?>pipeTile &&
                    pipeTile.getPipeType().getClass() == this.getPipeType().getClass()) {
                if (pipeTile.getNumConnections() >= 2) return;
            }
        }
        super.setConnection(side, connected, fromNeighbor);
    }

    public boolean isActive() {
        return this.isActive;
    }

    /**
     * @param active   if the pipe should become active
     * @param duration how long the pipe should be active for
     */
    public void setActive(boolean active, int duration) {
        boolean stateChanged = false;
        if (this.isActive && !active) {
            this.isActive = false;
            stateChanged = true;
        } else if (!this.isActive && active) {
            this.isActive = true;
            stateChanged = true;
            TaskScheduler.scheduleTask(getWorld(), () -> {
                if (++this.ticksActive % duration == 0) {
                    this.ticksActive = 0;
                    setActive(false, -1);
                    return false;
                }
                return true;
            });
        }

        if (stateChanged) {
            writeCustomData(NTDataCodes.PIPE_RESEARCH_ACTIVE, buf -> buf.writeBoolean(this.isActive));
            notifyBlockUpdate();
            markDirty();
        }
    }

    @Override
    public void receiveCustomData(int discriminator, PacketBuffer buf) {
        super.receiveCustomData(discriminator, buf);
        if (discriminator == NTDataCodes.PIPE_RESEARCH_ACTIVE) {
            this.isActive = buf.readBoolean();
            scheduleChunkForRenderUpdate();
        }
    }

    private static class DefaultComputationHandler implements ISCHResearchComputationProvider {

        @Override
        public int requestSCHRWUt(int schrwut, boolean simulate, @NotNull Collection<ISCHResearchComputationProvider> seen) {
            return 0;
        }

        @Override
        public int getMaxSCHRWUt(@NotNull Collection<ISCHResearchComputationProvider> seen) {
            return 0;
        }

        @Override
        public boolean canBridge(@NotNull Collection<ISCHResearchComputationProvider> seen) {
            return false;
        }
    }
}