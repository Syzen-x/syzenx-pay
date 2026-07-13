package dev.syzenx.syzenx_pay.infrastructure.adapter.out.db;

import dev.syzenx.syzenx_pay.domain.model.Wallet;
import dev.syzenx.syzenx_pay.domain.ports.out.WalletRepositoryPort;
import dev.syzenx.syzenx_pay.infrastructure.entity.WalletEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class WalletPostgresAdapter implements WalletRepositoryPort {

    private final SpringDataWalletRepository repository;

    public WalletPostgresAdapter(SpringDataWalletRepository repository) {
        this.repository = repository;
    }


    @Override
    public Optional<Wallet> findById(Long id) {
        return repository.findByIdForUpdate(id)
                .map(this::toDomain);
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = toEntity(wallet);
        WalletEntity saved = repository.save(entity);
        return toDomain(saved);
    }



    private Wallet toDomain(WalletEntity entity) {
        return new Wallet(entity.getId(), entity.getUserId(), entity.getBalance());
    }

    private WalletEntity toEntity(Wallet wallet) {
        WalletEntity entity = new WalletEntity();
        entity.setId(wallet.getId());
        entity.setUserId(wallet.getUserId());
        entity.setBalance(wallet.getBalance());
        return entity;
    }
}
