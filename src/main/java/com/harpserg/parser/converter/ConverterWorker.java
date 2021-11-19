package com.harpserg.parser.converter;

import com.google.gson.Gson;
import com.harpserg.parser.mapper.OrderMapper;
import com.harpserg.parser.model.Message;
import com.harpserg.parser.model.MessageType;
import com.harpserg.parser.service.Printers.Printer;
import com.harpserg.parser.service.Printers.impl.ConsolePrinter;

import java.util.concurrent.BlockingQueue;

public class ConverterWorker implements Runnable {

    private BlockingQueue<Message> messages;

    private Gson gson = new Gson();
    private OrderMapper orderMapper = new OrderMapper();
    private Printer printer = new ConsolePrinter();

    public ConverterWorker(BlockingQueue<Message> messages) {
        this.messages = messages;
    }

    public void run() {
        try {
            while (true) {
                Message message = messages.take();
                if (message.getMessageType() == MessageType.POISON_PILL)
                    return;
                if (message.getPayload().getError() == null)
                    printer.OutputInfoMessage(gson.toJson(orderMapper.OrderEntryToOrderOutput(message.getPayload())));
                else
                    printer.OutputWarningMessage(gson.toJson(orderMapper.OrderEntryToOrderOutput(message.getPayload())));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
