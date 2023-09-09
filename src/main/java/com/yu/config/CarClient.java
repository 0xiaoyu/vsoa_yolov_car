package com.yu.config;

import com.acoinfo.vsoa.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * &author  yu
 * 客户端配置文件
 */
@Configuration
@Slf4j
public class CarClient {

    /**
     * 客户端配置
     * @return Client
     */
    @Bean
    public Client client(){
        Client client = new Client(new ClientOption("123456",6000,4000,3,false));
        VsoaSocketAddress address = Position.lookup(
                new InetSocketAddress("192.168.116.130", 3000), "automobile_dash_board", 0);
        while (!client.connect(address, null, Constant.VSOA_DEF_CONN_TIMEOUT)) {
            log.error("Connected with server failed" + address);
        }
        return client;
    }
}
