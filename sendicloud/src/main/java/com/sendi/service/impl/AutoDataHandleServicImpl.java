package com.sendi.service.impl;

import com.sendi.service.UDPHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 处理自动上报的数据
 * Created by fengzm on 2019/2/1.
 */

@Service
public class AutoDataHandleServicImpl extends UDPHandleService {
    @Autowired
    private DataTypeServiceImpl dataTypeService;

    /**
     * 报文处理方法
     *
     * @param content
     * @param packet
     * @param socket
     */
    @Override
    public void doHandle(String content, DatagramPacket packet, DatagramSocket socket) {
        dataTypeService.doHandle( content,packet,socket);
    }





}
