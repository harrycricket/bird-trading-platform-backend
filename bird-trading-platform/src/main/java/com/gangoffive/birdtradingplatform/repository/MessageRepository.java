package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.entity.Message;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_message m " +
            "SET m.status = ?1 " +
            "WHERE m.channel_id = ?2 AND m.sender_id <> ?3 AND m.status =?4 ", nativeQuery = true)
    int updateStatusToSeen( String seenStatus ,long channelId,
                             long userId, String sentStatus);

    Long countByAccount_IdNotInAndStatusInAndChannelIn(List<Long> userId, List<MessageStatus> statusList, List<Channel> channelList);

}
