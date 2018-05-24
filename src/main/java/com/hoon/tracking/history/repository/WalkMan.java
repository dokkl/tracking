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
public class WalkMan {
    private Long id;
    private String name;
}
