package dev.syzenx.syzenx_pay.application.service;

import dev.syzenx.syzenx_pay.domain.model.Wallet;
import dev.syzenx.syzenx_pay.domain.ports.in.ChargeWalletUseCase;
import dev.syzenx.syzenx_pay.domain.ports.out.WalletRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class ChargeWalletService implements ChargeWalletUseCase {


    private final WalletRepositoryPort walletRepository;

    public ChargeWalletService(WalletRepositoryPort walletRepository) {
        this.walletRepository= walletRepository;
    }

    @Override
    @Transactional
    public Wallet charge(Long walletId, BigDecimal amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet no encontrada"));

        wallet.deductFunds(amount);

        return walletRepository.save(wallet);
    }
}
