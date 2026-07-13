package dev.syzenx.syzenx_pay.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public record ChargeRequest(
    BigDecimal amount
){

}
