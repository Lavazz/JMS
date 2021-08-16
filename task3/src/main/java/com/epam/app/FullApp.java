package com.epam.app;

import com.epam.app.config.ConfigJMS;
import com.epam.app.service.ServiceOrderSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
@Import(ConfigJMS.class)
public class FullApp {

    @Autowired

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(FullApp.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        ServiceOrderSender service = new ServiceOrderSender();
        service.makeOrder(jmsTemplate);
    }
}
