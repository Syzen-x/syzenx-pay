package dev.syzenx.syzenx_pay.infrastructure.adapter.in.web;


import dev.syzenx.syzenx_pay.infrastructure.adapter.in.web.dto.ChargeRequest;
import dev.syzenx.syzenx_pay.infrastructure.adapter.out.db.SpringDataWalletRepository;
import dev.syzenx.syzenx_pay.infrastructure.entity.WalletEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.web.context.request.RequestAttributes;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestRestTemplate
public class WalletControllerIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:15-alpine");


    @Container
    @ServiceConnection
    static GenericContainer redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);



    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpringDataWalletRepository repository;


    private Long testWalletId;


    @BeforeEach
    void setUp() {
        repository.deleteAll();

        WalletEntity wallet = new WalletEntity();

        wallet.setUserId(99L);
        wallet.setBalance(new BigDecimal("200.00"));
        testWalletId = repository.save(wallet).getId();
    }


    @Test
    public void shouldChargeWallet_AndRejectDuplicate_WithIdempotencyKey() {
        String idempotencyKey = "test-uuid-1234";

        ChargeRequest requestBody = new ChargeRequest(new BigDecimal("50.00"));

        HttpHeaders headers = new HttpHeaders();

        headers.set("Idempotency-Key", idempotencyKey);
        HttpEntity request = new HttpEntity(requestBody, headers);

        String url = "/api/v1/wallets/" + testWalletId + "/charge";


        ResponseEntity<Map> firstResponse = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);


        assertEquals(HttpStatus.OK, firstResponse.getStatusCode());
        assertEquals(150.0, firstResponse.getBody().get("newBalance"));


        ResponseEntity<Map> secondResponse = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);


        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
        assertEquals("Transaccion duplicada. Esta operacion ya fue procesada", secondResponse.getBody().get("error"));


    }



    @Test
    public void shouldPreventRaceConditions_WhenMultipleConcurrentChargesOcurr() throws InterruptedException {
        // Given: We have 200$ | 5 Charges of 20$
        int concurrentRequests = 5;
        BigDecimal chargeAmount = new BigDecimal("20.00");



        // Threads
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(concurrentRequests);


        for (int i = 0; i < concurrentRequests; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    String idempotencyKey = UUID.randomUUID().toString();

                    ChargeRequest requestBody = new ChargeRequest(chargeAmount);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Idempotency-Key", idempotencyKey);

                    HttpEntity<ChargeRequest> request = new HttpEntity<>(requestBody, headers);

                    String url = "/api/v1/wallets/" + testWalletId + "/charge";
                    restTemplate.postForEntity(url, request, Map.class);



                }catch (Exception e) {

                }finally {
                    endLatch.countDown();
                }
            });

        }
        startLatch.countDown();

        endLatch.await();

        WalletEntity wallet = repository.findById(testWalletId).orElseThrow();
        assertEquals(new BigDecimal("100.0000"), wallet.getBalance());

    }


}
