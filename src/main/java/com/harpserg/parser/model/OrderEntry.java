package com.harpserg.parser.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderEntry {

    private long lineNumber;
    private String fileName;
    private String error;
    private Order payload;
}
