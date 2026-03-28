package com.trocap.doituong.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "beneficiary_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhomDoiTuong {

    @Id
    private String id;

    @Indexed(unique = true)
    private String tenDoiTuong;   // Tên nhóm: "Người khuyết tật", "Người cao tuổi"...

    private String moTa;          // Mô tả chi tiết

    private String category;      // THU_NHAP_THAP, KHUYET_TAT, NGUOI_GIA, TRE_EM, DON_THAN, HIV, THIEN_TAI

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
