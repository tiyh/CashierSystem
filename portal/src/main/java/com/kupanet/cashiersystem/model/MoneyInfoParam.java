package com.kupanet.cashiersystem.model;



import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MoneyInfoParam {
    private Long orderId;
    private BigDecimal discountAmount;
    private Integer status;
}
