package com.github.cupangclone.web.dto.items;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ItemsRequest {

    private String itemName;
    private String itemExplain;
    private Long itemPrice;
    private Long itemStock;

}
