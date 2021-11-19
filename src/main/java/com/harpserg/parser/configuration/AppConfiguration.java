package com.harpserg.parser.configuration;

import com.google.gson.Gson;
import com.harpserg.parser.model.ProducerType;
import com.harpserg.parser.service.producers.OrdersProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AppConfiguration {

    @Autowired
    List<OrdersProducer> ordersProducers;

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public Map<ProducerType, OrdersProducer> ordersProducers() {
        return ordersProducers.stream().collect(Collectors.toMap(OrdersProducer::getType, Function.identity()));
    }

}
