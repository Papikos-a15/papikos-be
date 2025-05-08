package id.ac.ui.cs.advprog.papikosbe.repository.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    private UUID dummyUserId;
    private UUID dummyKosId;
    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        repository.deleteAll();    // reset store sebelum tiap test

        dummyUserId    = UUID.randomUUID();
        dummyKosId     = UUID.randomUUID();
        sampleBooking  = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(1))
                .duration(3)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        repository.save(sampleBooking);
        Optional<Booking> retrieved = repository.findById(sampleBooking.getBookingId());
        assertTrue(retrieved.isPresent());
        assertEquals(sampleBooking.getBookingId(), retrieved.get().getBookingId());
    }

    @Test
    void testFindByIdReturnsEmptyIfNotFound() {
        Optional<Booking> retrieved = repository.findById(UUID.randomUUID());
        assertFalse(retrieved.isPresent());
    }

    @Test
    void testFindByUserId() {
        Booking b1 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId).kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(1))
                .duration(3).status(BookingStatus.PENDING_PAYMENT).build();
        Booking b2 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId).kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(2))
                .duration(2).status(BookingStatus.PENDING_PAYMENT).build();
        Booking b3 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(UUID.randomUUID()).kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(3))
                .duration(4).status(BookingStatus.PENDING_PAYMENT).build();

        repository.saveAll(List.of(b1, b2, b3));

        List<Booking> userBookings = repository.findByUserId(dummyUserId);
        assertEquals(2, userBookings.size());
        userBookings.forEach(b -> assertEquals(dummyUserId, b.getUserId()));
    }

    @Test
    void testDeleteById() {
        repository.save(sampleBooking);
        assertTrue(repository.findById(sampleBooking.getBookingId()).isPresent());
        repository.deleteById(sampleBooking.getBookingId());
        assertFalse(repository.findById(sampleBooking.getBookingId()).isPresent());
    }

    @Test
    void testFindAllBookings() {
        Booking b1 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId).kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(1))
                .duration(1).status(BookingStatus.PENDING_PAYMENT).build();
        Booking b2 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId).kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(2))
                .duration(2).status(BookingStatus.PENDING_PAYMENT).build();

        repository.saveAll(List.of(b1, b2));

        List<Booking> all = repository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(b1));
        assertTrue(all.contains(b2));
    }
}
