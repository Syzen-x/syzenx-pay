package dev.syzenx.syzenx_pay.domain.ports.out;

import dev.syzenx.syzenx_pay.domain.model.Wallet;

import java.util.Optional;

public interface WalletRepositoryPort {

    Optional<Wallet> findById(Long id);

    Wallet save(Wallet wallet);

}
