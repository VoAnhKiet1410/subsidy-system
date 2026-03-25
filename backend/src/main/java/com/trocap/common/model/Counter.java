package com.trocap.common.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Counter collection dùng để sinh mã tuần tự (auto-increment).
 * Mỗi document là 1 sequence riêng (vd: "hoso_seq").
 */
@Document(collection = "counters")
@Data
public class Counter {
    @Id
    private String id;       // tên sequence, vd: "hoso_seq"
    private long seq;        // giá trị hiện tại
}
