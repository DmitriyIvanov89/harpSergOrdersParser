package com.harpserg.parser.service.producers;

import com.harpserg.parser.exception.OrdersProducerIOException;
import com.harpserg.parser.model.OrderEntry;
import com.harpserg.parser.model.ProducerType;

import java.io.File;
import java.util.function.Consumer;


public interface OrdersProducer {

    void readOrders(File file, Consumer<OrderEntry> consumer) throws OrdersProducerIOException;

    ProducerType getType();
}
