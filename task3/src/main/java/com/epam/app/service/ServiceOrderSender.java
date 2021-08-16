package com.epam.app.service;

import com.epam.app.entity.Order;
import com.epam.app.entity.Type;
import com.epam.app.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceOrderSender {

    public static final String FAILED = "failed";
    private static final String ORDER_REJECTED = "orderRejected";
    private static final String ORDER_CONFIRMED = "orderConfirmed";
    private static final int MAX_AMOUNT = 1000;
    private static final String SELECTOR = "selector";
    private static final String CONFIRMED = "confirmed";

    public void makeOrder(JmsTemplate jmsTemplate) {

        log.info("Input data of your order.\n");
        Scanner in = new Scanner(System.in);
        log.info("Input name: \n");
        String name = in.nextLine();
        log.info("Input lastname: \n");
        String lastname = in.nextLine();
        User user = new User(name, lastname);

        log.info("Liquids or countable item? Choose a number: \n1.liquids\n2.countable\n");
        int type = in.nextInt();
        Order order = null;
        if (type == 1) {
            int amount = orderLiquids(in);
            order = new Order(user, Type.LIQUID_ML, amount);

        }
        if (type == 2) {
            int amount = orderItems(in);
            order = new Order(user, Type.LIQUID_ML, amount);

        }
        in.close();

        boolean isOk = checkOrder(order);

        if (!isOk) {
            sendOrder(jmsTemplate, ORDER_REJECTED, order, false);
        } else {
            sendOrder(jmsTemplate, ORDER_CONFIRMED, order, true);
        }

    }

    private int orderLiquids(Scanner in) {
        log.info("Write value in ml:");
        return in.nextInt();
    }

    private int orderItems(Scanner in) {
        log.info("What number of items do you want?\n");
        return in.nextInt();
    }

    private void sendOrder(JmsTemplate jmsTemplate, String destination, Order order, boolean isConfirmed) {
        if (isConfirmed) {
            jmsTemplate.convertAndSend(destination, order,
                    messagePostProcessor -> {
                        messagePostProcessor.setStringProperty(SELECTOR,
                                CONFIRMED);
                        return messagePostProcessor;
                    });
        } else {
            jmsTemplate.convertAndSend(destination, order,
                    messagePostProcessor -> {
                        messagePostProcessor.setStringProperty(SELECTOR,
                                FAILED);
                        return messagePostProcessor;
                    });
        }
    }

    private boolean checkOrder(Order order) {
        if (order.getAmount() > ServiceOrderSender.MAX_AMOUNT) {
            log.info("Sorry. You can't make the order on more then 1000 ml or 100 items");
            return false;
        } else {
            return true;
        }
    }

}
