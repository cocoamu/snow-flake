package com.example.snowflake;

import com.example.snowflake.service.IdManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

@SpringBootTest
class SnowFlakeApplicationTests {

    @Autowired
    private IdManager idManager;

    @Test
    void contextLoads() {
        String uniId = idManager.nextId();
        System.out.println("获取单个唯一id：" + uniId);


        Random rd = new Random();
        long workerId = rd.nextInt(31);
        long datacenterId = rd.nextInt(31); //工作id，数据中心ID

        //获取当前ip,生成工作id
        String ip = "255.255.255.254";
        if(ip != null) {
            workerId = Long.parseLong(ip.replaceAll("\\.", ""));
            workerId = workerId % 256; //因为占用5位，模32
        }

        String uniId2 = idManager.nextId(datacenterId,workerId );
        System.out.println("获取单个唯一id(指定机器id)：" + uniId2);

        String[] ids = idManager.nextIds(10);
        for (int i = 0; i < ids.length; i++) {
            System.out.println(String.format("批量获取唯一id：第%d个,值是:%s",i,ids[i]));
        }
    }

    /**
     * 获取本机ip
     * @return
     */
    private static String getHostIp(){
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
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
