package com.example.snowflake.service.impl;

import com.example.snowflake.generator.SnowflakeIdGenerator;
import com.example.snowflake.service.IdManager;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SnowFlakeIdManager implements IdManager {

    private static final long DEFAULT_START_TIMESTAMP = 1483200000000L; // 2017-01-01 00:00:00:000

    private volatile Map<String, SnowflakeIdGenerator> idGeneratorMap = new ConcurrentHashMap<String, SnowflakeIdGenerator>();

    private final Environment environment;

    public SnowFlakeIdManager(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String nextId() {
        //默认数据中心id是5位的随机数，机器id是获取ip值再取模，最大也是31
        return nextId(new Random().nextInt(255),getMachineId());
    }

    @Override
    public String[] nextIds(Integer count) {
       return getIdGenerator(DEFAULT_START_TIMESTAMP, new Random().nextInt(255),getMachineId()).nextIds(count);
    }

    @Override
    public String nextId(long dataCenterId, long machineId) {
        return getIdGenerator(DEFAULT_START_TIMESTAMP, dataCenterId, machineId).nextId();
    }

    private SnowflakeIdGenerator getIdGenerator(long startTimestamp, long dataCenterId, long machineId) {
        String key = dataCenterId + "-" + machineId;

        SnowflakeIdGenerator idGenerator = idGeneratorMap.get(key);
        if (idGenerator == null) {
            SnowflakeIdGenerator newIdGnerator = new SnowflakeIdGenerator(startTimestamp, dataCenterId, machineId);
            idGenerator = idGeneratorMap.put(key, newIdGnerator);
            if (idGenerator == null) {
                idGenerator = newIdGnerator;
            }
        }

        return idGenerator;
    }


    /**
     * 获取本机ip
     * @return
     */
    private static long getMachineId(){
        long machineId = 1L;
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());

                        if(ip.getHostAddress() != null) {
                            machineId = Long.parseLong(ip.getHostAddress().replaceAll("\\.", ""));
                            machineId = machineId % 65535; //因为占用5位，模32
                        }
                        return machineId;
                    }
                }
            }
        }catch(Exception e){
           throw new RuntimeException("getMachineId failed",e);
        }
        return machineId;
    }


}
