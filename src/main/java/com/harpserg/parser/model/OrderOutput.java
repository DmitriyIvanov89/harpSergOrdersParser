package com.harpserg.parser.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderOutput {

    private Long id;
    private double amount;
    private String comment;
    private String filename;
    private long line;
    private String result;
}
