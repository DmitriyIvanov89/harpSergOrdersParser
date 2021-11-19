package com.harpserg.parser.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order {

    private Long orderId;
    private double amount;
    private Currency currency;
    private String comment;
}
