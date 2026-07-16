package dev.syzenx.syzenx_pay.application.service;


import dev.syzenx.syzenx_pay.domain.model.Wallet;
import dev.syzenx.syzenx_pay.domain.ports.out.WalletRepositoryPort;
import jakarta.inject.Inject;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChargeWalletServiceTest
{


    @Mock
    private WalletRepositoryPort walletRepositoryPort;

    @InjectMocks
    private ChargeWalletService chargeWalletService;


    @Test
    public void charge_ShouldReturnUpdatedWallet_WhenWalletExistsAndHasFunds() {
        // Given
        Long walletId = 1L;

        BigDecimal chargeAmount = new BigDecimal("20.00");
        Wallet mockWallet = new Wallet(walletId, 100L, new BigDecimal("100.00"));

        when(walletRepositoryPort.findById(walletId)).thenReturn(Optional.of(mockWallet));


        when(walletRepositoryPort.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = chargeWalletService.charge(walletId, chargeAmount);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("80.00"), result.getBalance());


        verify(walletRepositoryPort, times(1)).save(mockWallet);
    }


}
