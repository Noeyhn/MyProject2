package com.github.cupangclone.web.dto.items;

import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ItemsResponse {

    private Long itemId;
    private String sellerName;
    private String itemName;
    private String itemExplain;
    private Long itemPrice;
    private Long itemStock;

    public static ItemsResponse fromItem(Items item, UserPrincipal seller) {
        return ItemsResponse
                .builder()
                .itemId(item.getItemId())
                .sellerName(seller.getEmail())
                .itemName(item.getItemName())
                .itemExplain(item.getItemExplain())
                .itemPrice(item.getItemPrice())
                .itemStock(item.getItemStock())
                .build();
    }

}
