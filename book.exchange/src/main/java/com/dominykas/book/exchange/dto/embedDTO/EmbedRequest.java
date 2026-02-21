package com.dominykas.book.exchange.dto.embedDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbedRequest {
    private String text;
    private String modelKey;
}
