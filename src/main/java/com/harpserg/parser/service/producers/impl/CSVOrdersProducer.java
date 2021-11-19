package com.harpserg.parser.service.producers.impl;

import com.harpserg.parser.exception.OrdersProducerIOException;
import com.harpserg.parser.model.Currency;
import com.harpserg.parser.model.Order;
import com.harpserg.parser.model.OrderEntry;
import com.harpserg.parser.model.ProducerType;
import com.harpserg.parser.service.producers.OrdersProducer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

@Service
public class CSVOrdersProducer implements OrdersProducer {

    public void readOrders(File csvFile, Consumer<OrderEntry> consumer) throws OrdersProducerIOException {

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
            String[] values;
            long lineNumber = 0;
            while ((values = csvReader.readNext()) != null) {
                OrderEntry nextOrderEntry;
                try {
                    Order order = toOrder(values);
                    nextOrderEntry = new OrderEntry(lineNumber, csvFile.getName(), null, order);
                } catch (RuntimeException e) {
                    nextOrderEntry = new OrderEntry(lineNumber, csvFile.getName(), e.getMessage(), null);
                }

                consumer.accept(nextOrderEntry);
                lineNumber++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new OrdersProducerIOException(String.format("Error occurred while processing file %s: %s", csvFile.getName(), e.getMessage()), e);
        }
    }

    private Order toOrder(String[] values) {
        StringBuilder errors = new StringBuilder();
        Order.OrderBuilder orderBuilder = Order.builder();

        try {
            orderBuilder.orderId(Long.parseLong(values[0]));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        try {
            orderBuilder.amount(Double.parseDouble(values[1]));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        try {
            orderBuilder.currency(Currency.valueOf(values[2]));
        } catch (RuntimeException e) {
            if (errors.length() > 0)
                errors.append("  ,  ");
            errors.append(e);
        }

        try {
            orderBuilder.comment(values[3]);
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
        return ProducerType.CSV;
    }
}
