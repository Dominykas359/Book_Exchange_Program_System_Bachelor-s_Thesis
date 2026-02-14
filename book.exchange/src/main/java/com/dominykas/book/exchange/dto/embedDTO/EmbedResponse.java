package com.dominykas.book.exchange.dto.embedDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbedResponse {

    private List<Double> vector;
    private Integer dim;
    private String model;
}
