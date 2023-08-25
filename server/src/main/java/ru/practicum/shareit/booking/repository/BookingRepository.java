package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.StatusType;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerOrderByStartDesc(User user, Pageable pageable); // возвращает список всех
    // бронирований пользователя, отсортированных по времени начала, начиная с самого позднего.

    List<Booking> findBookingByBookerAndStatusOrderByStartDesc(User user, StatusType state,
            Pageable pageable); // возвращает список
    // всех бронирований пользователя с определённым StatusType, отсортированных по времени начала, начиная с самого позднего

    List<Booking> findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime dateTime,
            LocalDateTime dateTime1,
            Pageable pageable); // возвращает список всех бронирований пользователя, которые происходят
    // в указанный промежуток времени, отсортированных по времени начала, начиная с самого позднего.

    List<Booking> findBookingByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime dateTime,
            Pageable pageable); //  находит
    // все бронирования, которые были сделаны определенным пользователем и заканчиваются до указанного времени.
    // Бронирования отсортированы по времени начала в обратном порядке, начиная с самого позднего.

    List<Booking> findBookingByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime dateTime,
            Pageable pageable); // находит
    // все бронирования, которые были сделаны определенным пользователем и начинаются после указанного времени.
    // Бронирования отсортированы по времени начала в обратном порядке, начиная с самого позднего.

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.status = ?2 order by B.start desc")
    List<Booking> getBookingsForOwnerByStatus(Long ownerId, StatusType status, Pageable pageable); //  все бронирования,
    // для вещей User Owner и имеют указанный статус. Бронирования отсортированы по времени начала в обратном порядке,
    // начиная с самого позднего.

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 ORDER BY B.start DESC")
    List<Booking> getAllBookingsForOwner(Long ownerId, Pageable pageable); //  все бронирования, для вещей User Owner.
    // Бронирования отсортированы по времени начала в обратном порядке, начиная с самого позднего.

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.start < ?2 AND B.end > ?3 ORDER BY B.start DESC")
    List<Booking> getCurrentBookingForOwner(Long ownerId, LocalDateTime date1, LocalDateTime date2, Pageable pageable);
    // все ТЕКУЩИЕ бронирования, для вещей User Owner. Бронирования отсортированы по времени начала в обратном порядке,
    // начиная с самого позднего.

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.end < ?2 ORDER BY B.start DESC")
    List<Booking> getPastBookingForOwner(Long ownerId, LocalDateTime date, Pageable pageable);
    // все ПРОШЛЫЕ бронирования, для вещей User Owner. Бронирования отсортированы по времени начала в обратном порядке,
    // начиная с самого позднего.

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.start > ?2 ORDER BY B.start DESC")
    List<Booking> getFutureBookingForOwner(Long ownerId, LocalDateTime date, Pageable pageable);
    // все БУДУЩИЕ бронирования, для вещей User Owner. Бронирования отсортированы по времени начала в обратном порядке,
    // начиная с самого позднего.

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBefore(Long userId, Long itemId, LocalDateTime localDateTime);
    // все ОКОНЧЕННЫЕ бронирования для данного пользователя для данного предмета.

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(long itemId, LocalDateTime end,
            StatusType statusType); // находит последнее оконченное бронирование для данного предмета

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime start,
            StatusType statusType); // находит ближайшее будущее бронирование для данного предмета

}

