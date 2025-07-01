package org.secureaid.util;

import org.secureaid.dto.DonationRequestDTO;
import org.secureaid.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class ConcurrentDonationTest {
    @Autowired
    DonationService donationService;

    @Test
    void simulateConcurrentDonations() throws InterruptedException {
        int threadCount = 10;
        int donationsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int threadNum = i;
            executor.submit(() -> {
                for (int j = 0; j < donationsPerThread; j++) {
                    DonationRequestDTO dto = new DonationRequestDTO();
                    dto.setDonorName("User" + threadNum);
                    dto.setContact("user" + threadNum + "@example.com");
                    dto.setType("CASH");
                    dto.setAmount(new BigDecimal("10.00"));
                    donationService.addDonation(dto);
                }
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
        System.out.println("Total donations: " + donationService.getAllDonations().size());
    }
} 