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

    private static final String ORDER_REJECTED = "orderRejected";
    private static final String ORDER_CONFIRMED = "orderConfirmed";
    private static final int MAX_AMOUNT = 1000;

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

        if (order.getAmount() > 0 && order.getAmount() <= MAX_AMOUNT) {
            sendOrder(jmsTemplate, ORDER_CONFIRMED, order);
        } else if (order.getAmount() > MAX_AMOUNT) {
            log.info("Sorry. You can't make the order on more then 1000 ml or 1000 items");
            sendOrder(jmsTemplate, ORDER_REJECTED, order);
        } else {
            throw new IllegalArgumentException("amount should be more than 0");
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

    private void sendOrder(JmsTemplate jmsTemplate, String destinationName, Order order) {
        jmsTemplate.convertAndSend(destinationName, order);
        log.info("Order sent " + order);
    }

}
