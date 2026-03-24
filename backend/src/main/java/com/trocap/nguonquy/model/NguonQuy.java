package com.trocap.nguonquy.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "nguon_quy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguonQuy {

    @Id
    private String id;

    private String tenNguonQuy;
    private String moTa;
    private Double tongNganSach;
    private Double daSuDung;
    private Double conLai;

    private String loai; // NGAN_SACH_NHA_NUOC, TAI_TRO, XA_HOI_HOA

    private String trangThai; // ACTIVE, INACTIVE

    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Loai {
        NGAN_SACH_NHA_NUOC,
        TAI_TRO,
        XA_HOI_HOA
    }
}
