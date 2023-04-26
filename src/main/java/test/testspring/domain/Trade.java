package test.testspring.domain;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade")
@NoArgsConstructor
@Data
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seller_id")
    private String seller_id;

    @Column(name = "buyer_id")
    private String buyer_id;

    @Column(name = "product_no")
    private Long product_no;

    @Column(name = "trade_amount")
    private int trade_amount;

    @Column(name = "total_price")
    private BigDecimal total_price;

    @Column(name = "trade_at")
    private LocalDateTime trade_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", insertable = false, updatable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", insertable = false, updatable = false)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", insertable = false, updatable = false)
    private Product product;

    @Builder
    public Trade(String sellerId, String buyerId, Long productNo, int tradeAmount, BigDecimal totalPrice) {
        this.seller_id = sellerId;
        this.buyer_id = buyerId;
        this.product_no = productNo;
        this.trade_amount = tradeAmount;
        this.total_price = totalPrice;
        this.trade_at = LocalDateTime.now();
    }
}