package com.trocap.common.service;

import com.trocap.common.model.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Service sinh mã tuần tự (auto-increment) cho MongoDB.
 * Sử dụng findAndModify atomic để tránh race condition.
 */
@Service
@RequiredArgsConstructor
public class CounterService {

    private final MongoTemplate mongoTemplate;

    /**
     * Lấy giá trị tiếp theo cho sequence.
     * @param seqName tên sequence (vd: "hoso_seq")
     * @return giá trị tiếp theo (1, 2, 3, ...)
     */
    public long getNextSequence(String seqName) {
        Counter counter = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Counter.class
        );
        return counter != null ? counter.getSeq() : 1;
    }
}
