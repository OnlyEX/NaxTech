package com.onlyex.naxtech.api.capability.hatch.research.supradimension;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 用于表示任何使用 Transmitter Computation Hatches（传输计算舱）的多方块结构。
 * 该接口为多方块结构提供了计算相关的功能。
 */
public interface ISDIResearchComputationProvider{

    /**
     * requestSDIRWUt(int sdirwut, boolean simulate)：
     * 默认方法，实现该接口的类可以通过此方法请求每刻的最大 SDIRWU/t（每刻的计算工作单位）。
     * simulate 参数表示是否模拟请求，方法返回可供的 SDIRWU/t 数量。
     */
    default int requestSDIRWUt(int sdirwut, boolean simulate) {
        Collection<ISDIResearchComputationProvider> list = new ArrayList<>();
        list.add(this);
        return requestSDIRWUt(sdirwut, simulate, list);
    }

    /**
     * requestSDIRWUt(int sdirwut, boolean simulate, @NotNull Collection<IResearchComputationProvider> seen)：
     * 用于请求每刻的最大 SDIRWU/t。
     * 该方法需要实现类提供 sdirwut 和 simulate 参数的处理逻辑，并返回可供的 SDIRWU/t 数量。
     * seen 参数表示已经检查过的光学计算提供者。
     */
    int requestSDIRWUt(int sdirwut, boolean simulate, @NotNull Collection<ISDIResearchComputationProvider> seen);

    /**
     * getMaxSDIRWUt()：
     * 默认方法，返回该计算提供者可以提供的最大 SDIRWU/t 数量。
     */
    default int getMaxSDIRWUt() {
        Collection<ISDIResearchComputationProvider> list = new ArrayList<>();
        list.add(this);
        return getMaxSDIRWUt(list);
    }

    /**
     * getMaxSDIRWUt(@NotNull Collection<IResearchComputationProvider> seen)：
     * 返回该计算提供者可以提供的最大 SDIRWU/t 数量。
     * seen 参数表示已经检查过的光学计算提供者。
     */
    int getMaxSDIRWUt(@NotNull Collection<ISDIResearchComputationProvider> seen);

    /**
     * canBridge()：
     * 默认方法，返回该计算提供者是否可以与其他计算提供者“桥接”的布尔值。
     */
    default boolean canBridge() {
        Collection<ISDIResearchComputationProvider> list = new ArrayList<>();
        list.add(this);
        return canBridge(list);
    }

    /**
     * canBridge(@NotNull Collection<IResearchComputationProvider> seen)：
     * 返回该计算提供者是否可以与其他计算提供者“桥接”的布尔值。
     * seen 参数表示已经检查过的光学计算提供者。
     */
    boolean canBridge(@NotNull Collection<ISDIResearchComputationProvider> seen);
}