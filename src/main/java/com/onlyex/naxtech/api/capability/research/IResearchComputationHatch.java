package com.onlyex.naxtech.api.capability.research;

public interface IResearchComputationHatch extends IResearchComputationProvider {

    /** 注释指出了 isTransmitter() 方法用于判断该数据访问孔（Data Access Hatch）是否传输或接收 CWU/t */
    boolean isTransmitter();
}
