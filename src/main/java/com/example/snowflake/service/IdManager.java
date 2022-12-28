package com.example.snowflake.service;

public interface IdManager {

    String DATA_CENTER_ID = "data.center.id";
    String DATA_MACHINE_ID = "data.machine.id";

    /**
     * 获取单个唯一id
     * @return
     */
    String nextId();

    /**
     * 批量获取唯一id
     * @param count 获取个数
     * @return
     */
    String[] nextIds( Integer count);

    /**
     * 默认是从2017-01-01 开始
     *
     * @param dataCenterId
     * @param machineId
     * @return string 唯一标识码
     */
    String nextId(long dataCenterId, long machineId);




}