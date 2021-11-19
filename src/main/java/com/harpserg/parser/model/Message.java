package com.harpserg.parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {
    private MessageType messageType;
    private final OrderEntry payload;
}
