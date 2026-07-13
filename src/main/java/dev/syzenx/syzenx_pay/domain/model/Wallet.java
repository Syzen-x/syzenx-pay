package dev.syzenx.syzenx_pay.domain.model;

import java.math.BigDecimal;




public class Wallet {
    private Long id;
    private Long userId;
    private BigDecimal balance;



    public Wallet(Long id,  Long userId, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }


    public void addFunds(BigDecimal amount) {
       if (amount.compareTo(BigDecimal.ZERO) <= 0) {
           throw new IllegalArgumentException("El monto a recargar debe ser mayor que 0");
       }
       this.balance = this.balance.add(amount);
    }


    public void deductFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a cobrar debe ser mayor que 0");
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Fondos insuficientes en la billetera");
        }

        this.balance = this.balance.subtract(amount);

    }



    // Getters

    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public BigDecimal getBalance() {
        return balance;
    }

}
