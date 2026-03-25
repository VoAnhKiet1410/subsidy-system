package com.trocap.doituong.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "beneficiaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoiTuongHuongTruCap {

    @Id
    private String id;

    private String code;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String address;

    @Indexed
    private String idNumber; // CCCD

    @Indexed
    private String category;

    @Indexed
    private String status;

    private String programId;
    private Double subsidyAmount;
    private LocalDate startDate;

    private Integer aiTrustScore;

    private String reviewedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum Category {
        THU_NHAP_THAP,
        KHUYET_TAT,
        NGUOI_GIA,
        TRE_EM,
        DON_THAN,
        HIV,
        THIEN_TAI
    }

    public enum Status {
        ACTIVE,
        PENDING,
        PAUSED,
        REJECTED
    }
}
