package com.example.repick.payment;

import com.example.repick.domain.product.dto.productOrder.PostPayment;
import com.example.repick.domain.product.entity.*;
import com.example.repick.domain.product.repository.PaymentRepository;
import com.example.repick.domain.product.repository.ProductOrderRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.repository.ProductStateRepository;
import com.example.repick.domain.product.service.PaymentService;
import com.example.repick.global.entity.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStateRepository productStateRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setup() {
        // 테스트에 필요한 데이터 셋업
        product1 = Product.builder().discountPrice(1000L).build();
        product2 = Product.builder().discountPrice(2000L).build();
        productRepository.save(product1);
        productRepository.save(product2);

        ProductState productState1 = ProductState.builder()
                .productId(product1.getId())
                .productStateType(ProductStateType.SELLING)
                .build();
        ProductState productState2 = ProductState.builder()
                .productId(product2.getId())
                .productStateType(ProductStateType.SELLING)
                .build();
        productStateRepository.save(productState1);
        productStateRepository.save(productState2);
    }
    @Test
    public void testConcurrentPayments() throws InterruptedException {
        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < numberOfThreads; i++) {
            int userId = i + 1;
            tasks.add(() -> {
                System.out.println("Thread " + userId + " is ready.");
                latch.await(); // 모든 스레드가 준비될 때까지 대기
                try {
                    System.out.println("Thread " + userId + " is processing.");
                    Payment payment = Payment.builder()
                            .merchantUid("merchantUid" + userId)
                            .paymentStatus(PaymentStatus.READY)
                            .build();
                    paymentRepository.save(payment);

                    ProductOrder productOrder1 = ProductOrder.builder()
                            .payment(payment)
                            .userId((long) userId)
                            .productId(product1.getId())
                            .productOrderState(ProductOrderState.DEFAULT)
                            .build();
                    ProductOrder productOrder2 = ProductOrder.builder()
                            .payment(payment)
                            .userId((long) userId)
                            .productId(product2.getId())
                            .productOrderState(ProductOrderState.DEFAULT)
                            .build();
                    productOrderRepository.save(productOrder1);
                    productOrderRepository.save(productOrder2);

                    PostPayment postPayment = new PostPayment("iamportUid" + userId, "merchantUid" + userId, new Address());
                    return paymentService.validatePaymentForTest(postPayment);  // iamportClient 통신 부분 제외한 테스트용 코드 작성
                } catch (Exception e) {
                    System.out.println("Exception occurred while processing payment: " + userId + "\n" + e.getMessage());
                    return false;
                }
            });
        }

        System.out.println("All threads are ready to start.");
        latch.countDown(); // 모든 스레드가 동시에 실행되도록

        List<Future<Boolean>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("All threads are completed.");

        int successCount = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successCount++;
                }
            } catch (ExecutionException e) {
                System.out.println("ExecutionException occurred while processing future result: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 성공적으로 처리된 결제가 한 번이어야 함
        System.out.println("Number of successful payments: " + successCount);
        assertTrue(successCount == 1, "동시성 테스트 실패: 성공적으로 처리된 결제는 한 번이어야 합니다.");
    }
}
