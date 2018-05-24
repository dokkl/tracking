package com.hoon.tracking.history.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by babybong on 2018. 5. 3..
 */
@Repository
public interface BoxTrackingHistoryRepository extends ReactiveMongoRepository<BoxTrackingHistory, String> {
    Flux<BoxTrackingHistory> findByInvoiceNumber(String invoiceNumber);

    Flux<BoxTrackingHistory> findByShippingCompanyCodeAndInvoiceNumber(String shippingCompanyCode, String invoiceNumber);

    Flux<BoxTrackingHistory> findByInvoiceNumberAndDeliveryCode(String invoiceNumber, String deliveryCode);

}
