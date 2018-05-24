package com.hoon.tracking.history.repository;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by babybong on 2018. 5. 3..
 */
@Document
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Container {
    private String containerUsageId;
    private String containerGroup; //ContainerGroup
    private String containerType;
    private String containerBarcode;
    private String containerCode;
    private String machineCode;
    private String invoiceDone;

    private Workplace workplace;
}
