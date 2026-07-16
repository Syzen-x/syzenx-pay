package dev.syzenx.syzenx_pay.domain.model;




import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


public class WalletTest {

    @Test
    public void deductFunds_ShouldDecreaseBalance_WhenSufficientFundsExist() {
        //Given
        Wallet wallet = new Wallet(1L, 100L, new BigDecimal("150.00"));

        //When
        wallet.deductFunds(new BigDecimal("50.00"));

        //Then
       assertEquals(new BigDecimal("100.00"), wallet.getBalance());
    }

    @Test
    public void deductFunds_ShouldThrowException_WhenInsufficientFunds() {
        //Given
        Wallet wallet = new Wallet(1L, 100L, new BigDecimal("150.00"));
        //When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
             wallet.deductFunds(new BigDecimal("60.00"));
        });


       assertEquals("Fondos insuficientes en la billetera", exception.getMessage());
    }


}
