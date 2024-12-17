package com.github.cupangclone.service;

import com.github.cupangclone.config.security.JwtTokenProvider;
import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.items.ItemsRepository;
import com.github.cupangclone.repository.redis.RedisRepository;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import com.github.cupangclone.repository.userPrincipal.UserPrincipalRepository;
import com.github.cupangclone.repository.userPrincipalRoles.UserPrincipalRoles;
import com.github.cupangclone.repository.userPrincipalRoles.UserPrincipalRolesRepository;
import com.github.cupangclone.web.dto.items.ItemsRequest;
import com.github.cupangclone.web.dto.items.ItemsResponse;
import com.github.cupangclone.web.exceptions.NotAcceptException;
import com.github.cupangclone.web.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellItemService {

    private final UserPrincipalRepository userPrincipalRepository;
    private final UserPrincipalRolesRepository userPrincipalRolesRepository;
    private final ItemsRepository itemsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;


    @Transactional(transactionManager = "tmJpa1")
    public String RegisterItems(HttpServletRequest request, HttpServletResponse response, ItemsRequest itemsRequest) {

        String token = jwtTokenProvider.resolveToken(request);

        if ( redisRepository.findRedisToToken(token) ) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "잘못된 접근입니다.";

        } else {

            String email = jwtTokenProvider.getUsername(token);

            UserPrincipal user = userPrincipalRepository.findByEmail(email)
                    .orElseThrow( () -> new NotFoundException("유저를 찾을 수 없습니다.") );
            UserPrincipalRoles userRoles = userPrincipalRolesRepository.findById(user.getUserPrincipalId())
                    .orElseThrow( () -> new NotFoundException("유저 정보에 오류가 있습니다.") );

            Items items = Items.builder()
                    .userPrincipal(user)
                    .itemName(itemsRequest.getItemName())
                    .itemExplain(itemsRequest.getItemExplain())
                    .itemPrice(itemsRequest.getItemPrice())
                    .itemStock(itemsRequest.getItemStock())
                    .build();

            itemsRepository.save(items);

            return "성공적으로 판매 물품을 등록하였습니다.";
        }
    }

    public List<ItemsResponse> GetAllSellItems(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtTokenProvider.resolveToken(request);

        if ( redisRepository.findRedisToToken(token) ) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new NotAcceptException("잘못된 접근입니다.");

        } else {

            String email = jwtTokenProvider.getUsername(token);

            UserPrincipal user = userPrincipalRepository.findByEmail(email)
                    .orElseThrow( () -> new NotFoundException("유저를 찾을 수 없습니다.") );
            UserPrincipalRoles userRoles = userPrincipalRolesRepository.findById(user.getUserPrincipalId())
                    .orElseThrow( () -> new NotFoundException("유저 정보에 오류가 있습니다.") );

            List<Items> foundItemsById = itemsRepository.findByUserPrincipalId(user.getUserPrincipalId());

            return foundItemsById
                    .stream()
                    .map( item -> ItemsResponse.fromItem(item, user))
                    .toList();
        }

    }

    public String ChangeItemStock(HttpServletRequest request, HttpServletResponse response, Long itemId, Long stock) {

        String token = jwtTokenProvider.resolveToken(request);

        if ( redisRepository.findRedisToToken(token) ) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new NotAcceptException("잘못된 접근입니다.");

        } else {

            String email = jwtTokenProvider.getUsername(token);

            UserPrincipal user = userPrincipalRepository.findByEmail(email)
                    .orElseThrow( () -> new NotFoundException("유저를 찾을 수 없습니다.") );
            UserPrincipalRoles userRoles = userPrincipalRolesRepository.findById(user.getUserPrincipalId())
                    .orElseThrow( () -> new NotFoundException("유저 정보에 오류가 있습니다.") );

            Optional<Items> item = itemsRepository.findById(itemId);
            if ( item.isPresent() ) {
                Items itemEntity = item.get();
                itemEntity.setItemStock(stock);
                itemsRepository.save(itemEntity);
                return "성공적으로 수정이 완료되었습니다.";
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return "아이템을 찾을 수 없습니다.";
            }
        }

    }
}
