package com.developing.shop.orders.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableRabbit
@Configuration
public class RabbitConfig {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("localhost");

        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory());
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public Queue addItemQueue() {
        return new Queue("addItem");
    }

    @Bean
    public Queue deleteItemQueue() {
        return new Queue("deleteItem");
    }

    @Bean
    public Queue orderStatusQueue() {
        return new Queue("orderStatus");
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("orderExchange");
    }

    @Bean
    public Binding bindingAddItem(){
        return BindingBuilder.bind(addItemQueue()).to(directExchange()).with("add");
    }

    @Bean
    public Binding bindingDeleteItem(){
        return BindingBuilder.bind(deleteItemQueue()).to(directExchange()).with("delete");
    }

    @Bean
    public Binding bindingOrderStatus(){
        return BindingBuilder.bind(orderStatusQueue()).to(directExchange()).with("status");
    }
}
