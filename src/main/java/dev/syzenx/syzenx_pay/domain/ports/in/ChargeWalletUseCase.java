package dev.syzenx.syzenx_pay.domain.ports.in;

import dev.syzenx.syzenx_pay.domain.model.Wallet;

import java.math.BigDecimal;

public interface ChargeWalletUseCase {
    Wallet charge(Long walletId, BigDecimal amount);
}
