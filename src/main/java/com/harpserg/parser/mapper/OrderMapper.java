package com.harpserg.parser.mapper;

import com.harpserg.parser.model.OrderEntry;
import com.harpserg.parser.model.OrderOutput;


public class OrderMapper {

    public OrderOutput OrderEntryToOrderOutput(OrderEntry orderEntry) {
        OrderOutput.OrderOutputBuilder orderOutputBuilder = OrderOutput.builder();

        orderOutputBuilder
                .filename(orderEntry.getFileName())
                .line(orderEntry.getLineNumber())
                .result(orderEntry.getError() == null ? "OK" : orderEntry.getError());

        if (orderEntry.getError() == null && orderEntry.getPayload() != null) {
            orderOutputBuilder
                    .id(orderEntry.getPayload().getOrderId())
                    .amount(orderEntry.getPayload().getAmount())
                    .comment(orderEntry.getPayload().getComment());
        }

        return orderOutputBuilder.build();
    }
}
