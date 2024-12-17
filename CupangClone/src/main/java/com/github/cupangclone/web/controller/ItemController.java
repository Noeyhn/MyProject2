package com.github.cupangclone.web.controller;

import com.github.cupangclone.service.ItemService;
import com.github.cupangclone.web.dto.items.ItemsResponse;
import com.github.cupangclone.web.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/all")
    public List<ItemsResponse> requestAllItems(HttpServletRequest request, HttpServletResponse response) {

        return itemService.getAllItems(request, response);

    }

    @GetMapping("/search")
    public ItemsResponse requestSearchItems(@RequestParam("item_id") Long itemId, HttpServletRequest request, HttpServletResponse response) {
        return itemService.getItemsByName(request, response, itemId);
    }

}
