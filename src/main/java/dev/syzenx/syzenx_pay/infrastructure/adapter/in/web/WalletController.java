package dev.syzenx.syzenx_pay.infrastructure.adapter.in.web;


import dev.syzenx.syzenx_pay.domain.model.Wallet;
import dev.syzenx.syzenx_pay.domain.ports.in.ChargeWalletUseCase;
import dev.syzenx.syzenx_pay.infrastructure.adapter.in.web.dto.ChargeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final ChargeWalletUseCase chargeWalletUseCase;

    public WalletController(ChargeWalletUseCase chargeWalletUseCase) {
        this.chargeWalletUseCase = chargeWalletUseCase;
    }


    @PostMapping("/{id}/charge")
    public ResponseEntity chargeWallet(@PathVariable Long id, @RequestBody ChargeRequest request){
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
