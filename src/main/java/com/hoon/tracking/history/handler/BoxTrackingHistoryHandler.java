package com.hoon.tracking.history.handler;


import com.hoon.tracking.history.repository.BoxTrackingHistory;
import com.hoon.tracking.history.repository.BoxTrackingHistoryRepository;
import com.hoon.tracking.history.repository.Container;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.endsWith;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

/**
 * Created by babybong on 2018. 5. 3..
 */
@Slf4j
@Component
public class BoxTrackingHistoryHandler {

    @Autowired
    private BoxTrackingHistoryRepository repository;

    public Mono<ServerResponse> findBoxTrackingHistory(ServerRequest request) {
        String invoiceNumber = request.pathVariable("invoiceNumber");
        log.info(">> invoiceNumber : {}", invoiceNumber);
        Optional<String> deliveryCode = request.queryParam("deliveryCode");

        Flux<BoxTrackingHistory> boxTrackingHistoryFlux = null;
        if (deliveryCode.isPresent()) {
            log.info(">> deliveryCode : {}", deliveryCode.get());
            boxTrackingHistoryFlux = repository.findByInvoiceNumberAndDeliveryCode(invoiceNumber, deliveryCode.get());
        } else {
            log.info(">> deliveryCode 파라미터 없슴");
            boxTrackingHistoryFlux = repository.findByInvoiceNumber(invoiceNumber);
        }
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(boxTrackingHistoryFlux, BoxTrackingHistory.class)
                             .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> findBoxTrackingHistoryWithShippingComapyCode(ServerRequest request) {
        String shippingCompanyCode = request.pathVariable("shippingCompanyCode");
        String invoiceNumber = request.pathVariable("invoiceNumber");
        log.info(">> request pathVariable : {} {}", shippingCompanyCode, invoiceNumber);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Flux<BoxTrackingHistory> boxTrackingHistoryFlux
                = repository.findByShippingCompanyCodeAndInvoiceNumber(shippingCompanyCode, invoiceNumber);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(boxTrackingHistoryFlux, BoxTrackingHistory.class)
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> insertBoxTrackingHistory(ServerRequest request) {
        Mono<BoxTrackingHistory> boxTrackingHistoryMono = request.bodyToMono(BoxTrackingHistory.class);
        boxTrackingHistoryMono.doOnNext(history -> repository.save(history)).log("save !!!").subscribe();
        return ServerResponse.ok().build(Mono.empty());
    }

    public Mono<ServerResponse> getCount(ServerRequest request) {
        Mono<Long> countMono = repository.findAll().count();
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(countMono, Long.class);
        /*return countMono.flatMap(count -> ServerResponse.ok()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(countMono, Long.class));
                                                        //.syncBody(count));
                                                        //.body(fromObject(count)));*/
    }

    public Mono<ServerResponse> getTest(ServerRequest request) {
        BoxTrackingHistory bth = BoxTrackingHistory.builder()
                                                   .branchName("HUB")
                                                   //.branchName("인천4HUB")
                                                   .container(Container.builder()
                                                                       .containerCode("서울83바").build()).build();
        //Example<BoxTrackingHistory> example = Example.of(bth);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("branchName", endsWith())
                .withMatcher("container.containerCode", startsWith().ignoreCase());
        Example<BoxTrackingHistory> example = Example.of(bth, matcher);
        Flux<BoxTrackingHistory> boxTrackingHistoryFlux = repository.findAll(example);

        Mono<Long> monoCount = repository.count(example);
        monoCount.subscribe(cnt -> log.info("count : {}", cnt));

        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(boxTrackingHistoryFlux, BoxTrackingHistory.class)
                             .switchIfEmpty(notFound);
    }
}
