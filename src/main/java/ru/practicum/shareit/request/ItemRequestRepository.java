package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(Long userId);


    @Query(" SELECT ir FROM ItemRequest ir " +
            "WHERE ir.requester.id <> ?1 " +
            "order by ir.created")
    List<ItemRequest> findAll(Long requesterId, PageRequest pageRequest);

}
