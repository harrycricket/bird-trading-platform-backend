package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT COUNT(*) FROM tbl_message where channel_id = :id AND status IN :list  AND sender_id <> :userId"
            , nativeQuery = true)
    int countByIdAndListIn(@Param("id") Long id, @Param("list") List<String> list,
                           @Param("userId") long userId);

    Page<Message> findByChannel_Id(long id, Pageable pageable);
}
