package com.github.cupangclone.service;

import com.github.cupangclone.config.security.JwtTokenProvider;
import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.items.ItemsRepository;
import com.github.cupangclone.repository.redis.RedisRepository;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import com.github.cupangclone.repository.userPrincipal.UserPrincipalRepository;
import com.github.cupangclone.web.dto.items.ItemsResponse;
import com.github.cupangclone.web.exceptions.NotAcceptException;
import com.github.cupangclone.web.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemsRepository itemsRepository;
    private final UserPrincipalRepository userPrincipalRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;

    public List<ItemsResponse> getAllItems(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtTokenProvider.resolveToken(request);

        if ( redisRepository.findRedisToToken(token) ) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new NotAcceptException("잘못된 접근입니다.");

        } else {

            List<Items> foundedAllItems = itemsRepository.findAll();

            return foundedAllItems
                    .stream()
                    .map(items
                            -> ItemsResponse.fromItem(items, items.getUserPrincipal()))
                    .toList();

        }

    }

    public ItemsResponse getItemsByName(HttpServletRequest request, HttpServletResponse response, Long itemId) {

        String token = jwtTokenProvider.resolveToken(request);

        if ( redisRepository.findRedisToToken(token) ) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new NotAcceptException("잘못된 접근입니다.");

        } else {

            Optional<Items> itemById = itemsRepository.findById(itemId);

            if ( itemById.isPresent() ) {

                UserPrincipal user = itemById.get().getUserPrincipal();

                return ItemsResponse.fromItem(itemById.get(), user);

            } else {
                throw new NotFoundException(response, "물품을 찾을 수 없습니다.");
            }

        }

    }
}
