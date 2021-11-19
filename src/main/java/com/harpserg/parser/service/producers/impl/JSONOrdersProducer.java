package com.harpserg.parser.service.producers.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.harpserg.parser.exception.OrdersProducerIOException;
import com.harpserg.parser.model.Currency;
import com.harpserg.parser.model.Order;
import com.harpserg.parser.model.OrderEntry;
import com.harpserg.parser.model.ProducerType;
import com.harpserg.parser.service.producers.OrdersProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class JSONOrdersProducer implements OrdersProducer {

    @Autowired
    private Gson gson;

    public void readOrders(File jsonFile, Consumer<OrderEntry> consumer) throws OrdersProducerIOException {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            long lineNumber = 0;
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            while ((line = bufferedReader.readLine()) != null) {
                OrderEntry nextOrderEntry;
                try {
                    Order order = toOrder(gson.fromJson(line, type));
                    nextOrderEntry = new OrderEntry(lineNumber, jsonFile.getName(), null, order);
                } catch (RuntimeException e) {
                    nextOrderEntry = new OrderEntry(lineNumber, jsonFile.getName(), e.getMessage(), null);
                }

                consumer.accept(nextOrderEntry);
                lineNumber++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new OrdersProducerIOException(String.format("Error occurred while processing file %s: %s", jsonFile.getName(), e.getMessage()), e);
        }
    }

    private Order toOrder(Map<String, String> values) {
        StringBuilder errors = new StringBuilder();
        Order.OrderBuilder orderBuilder = Order.builder();

        try {
            if (values.get("orderId") != null)
                orderBuilder.orderId(Long.parseLong(values.get("orderId")));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        try {
            if (values.get("amount") != null)
                orderBuilder.amount(Double.parseDouble(values.get("amount")));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        try {
            if (values.get("currency") != null)
                orderBuilder.currency(Currency.valueOf(values.get("currency")));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        try {
            if (values.get("comment") != null)
                orderBuilder.comment(values.get("comment"));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        if (errors.toString().isEmpty()) {
            return orderBuilder.build();
        } else {
            throw new RuntimeException(errors.toString());
        }
    }

    @Override
    public ProducerType getType() {
        return ProducerType.JSONL;
    }
}
