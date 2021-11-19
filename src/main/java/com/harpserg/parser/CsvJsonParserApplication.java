package com.harpserg.parser;

import com.harpserg.parser.converter.ConverterWorker;
import com.harpserg.parser.exception.OrdersProducerIOException;
import com.harpserg.parser.model.Message;
import com.harpserg.parser.model.MessageType;
import com.harpserg.parser.model.ProducerType;
import com.harpserg.parser.service.producers.OrdersProducer;
import com.harpserg.parser.service.Printers.Printer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class CsvJsonParserApplication implements CommandLineRunner {

    @Autowired
    private Map<ProducerType, OrdersProducer> ordersProducers;

    @Autowired
    private Printer printer;

    private static final int QUEUE_CAPACITY = 1000;
    private static final int MAX_SIMULTANEOUS_PRODUCERS = 1;
    private static final int MAX_SIMULTANEOUS_CONSUMERS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        SpringApplication.run(CsvJsonParserApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (validateArgs(args)) {
            BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
            runConsumers(messageQueue);
            List<File> files = Arrays.stream(args).map(File::new).collect(Collectors.toList());
            runProducers(files, messageQueue);
        }
    }

    private void runConsumers(BlockingQueue<Message> messageQueue) {
        for (int j = 0; j < MAX_SIMULTANEOUS_CONSUMERS; j++) {
            new Thread(new ConverterWorker(messageQueue)).start();
        }
    }

    private void runProducers(List<File> files, BlockingQueue<Message> messageQueue) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_PRODUCERS);
        CountDownLatch producersLatch = new CountDownLatch(files.size());

        for (File file : files) {
            ProducerType producerType = ProducerType.getByName(getExtension(file).toUpperCase());
            executorService.execute(() -> {
                try {
                    ordersProducers.get(producerType).readOrders(file, orderEntry -> {
                        try {
                            messageQueue.put(new Message(MessageType.REGULAR, orderEntry));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    producersLatch.countDown();
                } catch (OrdersProducerIOException e) {
                    printer.OutputErrorMessage(e.getMessage());
                }
            });
        }

        try {
            producersLatch.await();
            executorService.shutdown();
            for (int i = 0; i < MAX_SIMULTANEOUS_CONSUMERS; i++) {
                messageQueue.put(new Message(MessageType.POISON_PILL, null));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean validateArgs(String... args) {
        for (String path : args) {
            File file = new File(path);

            if (!file.exists()) {
                printer.OutputErrorMessage(String.format("File doesn't exists! File name: %s", file.getName()));
                return false;
            }
            ProducerType producerType = ProducerType.getByName(getExtension(file).toUpperCase());
            if (producerType == ProducerType.UNKNOWN) {
                printer.OutputErrorMessage(String.format("Unsupported input file extension! File name: %s", file.getName()));
                return false;
            }
        }
        return true;
    }

    private String getExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }
}
