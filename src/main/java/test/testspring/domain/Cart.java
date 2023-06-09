package test.testspring.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cid")
    private Long cid;
    @Column(name="mid")
    private String mid;
    @Column(name="p_count")
    private int pCount;
    @Column(name="keep_at")
    private Date keep_at;
    @ManyToOne
    @JoinColumn(name="pno")
    private Product product;



}
