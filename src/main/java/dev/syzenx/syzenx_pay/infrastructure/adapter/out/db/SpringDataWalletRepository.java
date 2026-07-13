package dev.syzenx.syzenx_pay.infrastructure.adapter.out.db;

import dev.syzenx.syzenx_pay.domain.model.Wallet;
import dev.syzenx.syzenx_pay.infrastructure.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataWalletRepository extends JpaRepository<WalletEntity, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT w FROM WalletEntity w WHERE w.id = :id")
    Optional<WalletEntity> findByIdForUpdate(@Param("id") Long id);



}
