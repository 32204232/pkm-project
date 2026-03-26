package com.pkm.store.domain.cart.entity;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int count;

    @Builder
    public CartItem(Member member, Product product, int count) {
        this.member = member;
        this.product = product;
        this.count = count;
    }

   public void updateCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.count = count;
    }
}