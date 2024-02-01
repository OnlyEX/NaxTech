package com.onlyex.naxtech.api.capability.hatch.research;


import com.onlyex.naxtech.api.capability.hatch.research.rwu.IResearchComputationProvider;

/**
 * 这个方法用于获取计算提供者（Computation Provider）。
 * 返回一个 IResearchComputationProvider 接口类型的对象，用于提供计算相关的功能。
 */
public interface IResearchComputationReceiver {

    IResearchComputationProvider getResearchComputationProvider();
}