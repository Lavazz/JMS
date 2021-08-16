package com.epam.app.service;

import com.epam.app.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;

@Slf4j
@Component
public class ReceiverOrderService {

    private CountDownLatch latch = new CountDownLatch(2);

    public CountDownLatch getLatch() {
        return latch;
    }

    @JmsListener(destination = "orderConfirmed",
            selector = "selector = 'confirmed'")
    public void receiveHigh(Order order) {
       log.info("received confirmed order='{}'", order);
        writeOrderToFile("confirmed_order",order);
        latch.countDown();
    }

    @JmsListener(destination = "orderRejected",
            selector = "selector = 'failed'")
    public void receiveLow(Order order) {
       log.info("rejected order='{}'", order);
        writeOrderToFile("rejected_order",order);
        latch.countDown();
    }

    private void writeOrderToFile(String fileName, Order order) {
        File file = new File("C:\\Users\\Katsiaryna_Kaloshych\\train\\JMS\\task3\\src\\main\\resources\\" + fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()))) {
            writer.write(format("%s%n", order));
        } catch (IOException ex) {
           log.error("exception during write info to resources", ex);
        }
    }


}