package dev.syzenx.syzenx_pay.infrastructure.adapter.in.web;


import dev.syzenx.syzenx_pay.domain.model.Wallet;
import dev.syzenx.syzenx_pay.domain.ports.in.ChargeWalletUseCase;
import dev.syzenx.syzenx_pay.infrastructure.adapter.in.web.dto.ChargeRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final ChargeWalletUseCase chargeWalletUseCase;
    private final StringRedisTemplate redisTemplate;

    public WalletController(ChargeWalletUseCase chargeWalletUseCase, StringRedisTemplate redisTemplate) {
        this.chargeWalletUseCase = chargeWalletUseCase;
        this.redisTemplate = redisTemplate;
    }


    @PostMapping("/{id}/charge")
    public ResponseEntity chargeWallet(@PathVariable Long id,@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody ChargeRequest request){


        String redisKey = "idemp:charge:" + idempotencyKey;

        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(redisKey,"PROCESSED", Duration.ofHours(24));



        if (Boolean.FALSE.equals(isNewRequest)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Transaccion duplicada. Esta operacion ya fue procesada"));
        }



        try {
            Wallet updatedWallet = chargeWalletUseCase.charge(id, request.amount());


            //TO DO: ChargeResponse
            return ResponseEntity.ok(
                    Map.of(
                            "walletId", updatedWallet.getId(),
                            "newBalance", updatedWallet.getBalance()
                    )
            );
        }catch (IllegalArgumentException  | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
