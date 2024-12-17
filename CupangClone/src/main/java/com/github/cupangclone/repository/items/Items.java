package com.github.cupangclone.repository.items;

import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "items")
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_principal_id")
    private UserPrincipal userPrincipal;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "item_explain")
    private String itemExplain;

    @Column(name = "item_price")
    @ColumnDefault("'0'")
    private Long itemPrice;

    @Column(name = "item_stock")
    @ColumnDefault("'0'")
    private Long itemStock;

}
