package com.epam.app.service;

import com.epam.app.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReceiverOrderService {

    @JmsListener(destination = "orderConfirmed", containerFactory = "myFactory")
    public void receiveMessage(Order order ) {
        log.info("Confirmed order<" + order + ">");
    }

    @JmsListener(destination = "orderRejected", containerFactory = "myFactory")
    public void receiveRejectedMessage(Order order) {
        log.info("Rejected order <" + order + ">");
    }

}