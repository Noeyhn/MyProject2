package com.github.cupangclone.web.controller;

import com.github.cupangclone.service.SellItemService;
import com.github.cupangclone.web.dto.items.ItemsRequest;
import com.github.cupangclone.web.dto.items.ItemsResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sell_items")
@RequiredArgsConstructor
public class SellItemController {

    private final SellItemService sellItemService;

    @PostMapping("/register")
    public String RegisterItemsForSale(@RequestBody ItemsRequest itemsRequest, HttpServletRequest request, HttpServletResponse response) {
        return sellItemService.RegisterItems(request, response, itemsRequest);
    }

    @GetMapping("/all")
    public List<ItemsResponse> GetAllItemsBySeller(HttpServletRequest request, HttpServletResponse response) {
        return sellItemService.GetAllSellItems(request, response);
    }

    @PutMapping("/change")
    public String UpdateItemStock(@RequestParam("item_id") Long itemId, @RequestParam("stock") Long stock, HttpServletRequest request, HttpServletResponse response){
        return sellItemService.ChangeItemStock(request, response, itemId, stock);
    }

}
