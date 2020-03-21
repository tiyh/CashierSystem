package com.kupanet.cashiersystem.model;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class MoneyInfoParam  implements Serializable {
    private static final long serialVersionUID = -6978260222243660126L;
    private Long orderId;
    private BigDecimal discountAmount;
    private Integer status;
}
