package com.vaxjaz.polling.operation.enums;

public enum PollingStrategyEnum {

    /**
     * 一直固定频率
     */
    FIXED,

    /**
     * 如果有task,马上pull
     * 如果没有task,固定频率pull
     */
    FIXED_THEN_IMMEDIATELY

}
