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
    private double monthlyPrice;
    private String fullName;
    private String phoneNumber;

    @BeforeEach
    void setUp() {
        repository.deleteAll();    // reset store sebelum tiap test

        dummyUserId = UUID.randomUUID();
        dummyKosId = UUID.randomUUID();
        monthlyPrice = 1500000.0;
        fullName = "John Doe";
        phoneNumber = "081234567890";

        sampleBooking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(1))
                .duration(3)
                .monthlyPrice(monthlyPrice)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        repository.save(sampleBooking);
        Optional<Booking> retrieved = repository.findById(sampleBooking.getBookingId());
        assertTrue(retrieved.isPresent());
        assertEquals(sampleBooking.getBookingId(), retrieved.get().getBookingId());
        assertEquals(monthlyPrice, retrieved.get().getMonthlyPrice());
        assertEquals(fullName, retrieved.get().getFullName());
        assertEquals(phoneNumber, retrieved.get().getPhoneNumber());
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
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(1))
                .duration(3)
                .monthlyPrice(monthlyPrice)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        Booking b2 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(2))
                .duration(2)
                .monthlyPrice(monthlyPrice)
                .fullName("Jane Doe")
                .phoneNumber("089876543210")
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        Booking b3 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(3))
                .duration(4)
                .monthlyPrice(2000000.0)
                .fullName("Other User")
                .phoneNumber("087654321098")
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

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
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(1))
                .duration(1)
                .monthlyPrice(1200000.0)
                .fullName("User One")
                .phoneNumber("081111111111")
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        Booking b2 = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(LocalDate.now().plusDays(2))
                .duration(2)
                .monthlyPrice(1300000.0)
                .fullName("User Two")
                .phoneNumber("082222222222")
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        repository.saveAll(List.of(b1, b2));

        List<Booking> all = repository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(b1));
        assertTrue(all.contains(b2));
    }

    @Test
    void testTotalPriceCalculation() {
        // Test the derived total price calculation
        repository.save(sampleBooking);
        Optional<Booking> retrieved = repository.findById(sampleBooking.getBookingId());
        assertTrue(retrieved.isPresent());

        double expectedTotal = monthlyPrice * sampleBooking.getDuration();
        assertEquals(expectedTotal, retrieved.get().getTotalPrice());
    }

    @Test
    void testFindBookingsToDeactivate() {
        // Create an active booking with end date in the past
        LocalDate pastCheckIn = LocalDate.now().minusMonths(3);
        int duration = 2; // 2 months

        Booking expiredBooking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                pastCheckIn,
                duration,
                1500000.0,
                "Test User",
                "081234567890",
                BookingStatus.APPROVED
        );

        // Create an active booking that is still valid
        LocalDate currentCheckIn = LocalDate.now().minusMonths(1);
        int currentDuration = 3; // 3 months, still valid

        Booking validBooking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                currentCheckIn,
                currentDuration,
                1500000.0,
                "Active User",
                "082345678901",
                BookingStatus.APPROVED
        );

        // Create an already inactive booking
        Booking inactiveBooking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                pastCheckIn,
                duration,
                1500000.0,
                "Inactive User",
                "083456789012",
                BookingStatus.INACTIVE
        );

        // Save all bookings
        repository.save(expiredBooking);
        repository.save(validBooking);
        repository.save(inactiveBooking);

        // Test the finder method
        List<Booking> expiredBookings = repository.findBookingsToDeactivate(LocalDate.now());

        // Should only find the expired booking, not the valid one or already inactive one
        assertEquals(1, expiredBookings.size());
        assertEquals(expiredBooking.getBookingId(), expiredBookings.get(0).getBookingId());
    }
}