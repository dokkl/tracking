package com.hoon.tracking.history.repository;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Created by babybong on 2018. 5. 3..
 */
@Document
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxTrackingHistory {
    @Id
    private String id;
    private String invoiceNumber;
    private String shippingCompanyCode;
    private String deliveryCode;
    private String deliveryMessage;
    private String reasonCode;
    private String reasonMessage;
    private String branchName;
    private String branchPhoneNumber;
    private String staffName;
    private String staffMobileNumber;
    private LocalDateTime scannedAt;
    private LocalDateTime registeredAt;
    private String invoiceType; //TransportInvoiceType
    private String uniqueCode;
    private String providerTrackingId;
    private String providerName; //ProviderName
    private String orderId;
    private CoupangMan coupangMan; //배송출발시 by cdm
    private WalkMan walkMan;       //배송완료, 미배송, 잔류, 미회수, 회수완료 시 by cdm
    private Container container;   //간선 컨테이너(트럭) 정보 by HLM

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
    private String createdBy;
}
