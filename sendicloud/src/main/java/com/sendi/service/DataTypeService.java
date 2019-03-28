package com.sendi.service;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

/**
 * Created by fengzm on 2019/2/2.
 */
public interface DataTypeService {

    /**
     * //数据类型为数值类型
     *
     * @param map
     * @param devId
     * @param resId
     */
     void handleType01(Map<String, Object> map, BigInteger devId, Integer resId) ;

    /**
     * //数据类型为字符串类型
     *
     * @param map
     * @param devId
     * @param resId
     */

     void handleType02(Map<String, Object> map, BigInteger devId, Integer resId) ;

    /**
     * 数据类型为图片类型器 0x50
     *
     * @param map
     * @param devId
     * @param resId
     * @param packet
     * @param socket
     */
     void handleType03(Map<String, Object> map, BigInteger devId, Integer resId, DatagramPacket packet, DatagramSocket socket) ;
    /**
     * 展示类型
     * @param map
     * @param devId
     * @param resId
     * @param packet
     * @param socket
     */
    void handleType04(Map<String, Object> map, BigInteger devId, Integer resId, DatagramPacket packet, DatagramSocket socket) ;
}
