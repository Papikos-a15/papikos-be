package id.ac.ui.cs.advprog.papikosbe.model.booking;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

public class BookingTest {

    private Booking booking;
    private UUID dummyUserId;
    private UUID dummyKosId;
    private double monthlyPrice;
    private String fullName;
    private String phoneNumber;
    private LocalDate validCheckInDate;

    @BeforeEach
    public void setUp() {
        dummyUserId = UUID.randomUUID();
        dummyKosId = UUID.randomUUID();
        monthlyPrice = 1000000.0;
        fullName = "John Doe";
        phoneNumber = "081234567890";
        validCheckInDate = LocalDate.now().plusDays(1);

        booking = new Booking(
                UUID.randomUUID(),
                dummyUserId,
                dummyKosId,
                validCheckInDate,
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );
    }

    // ===== BASIC FUNCTIONALITY TESTS =====

    @Test
    public void testBookingInitialization() {
        assertNotNull(booking.getBookingId());
        assertEquals(dummyUserId, booking.getUserId());
        assertEquals(dummyKosId, booking.getKosId());
        assertEquals(validCheckInDate, booking.getCheckInDate());
        assertEquals(3, booking.getDuration());
        assertEquals(monthlyPrice, booking.getMonthlyPrice());
        assertEquals(fullName, booking.getFullName());
        assertEquals(phoneNumber, booking.getPhoneNumber());
        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
    }

    @Test
    public void testGetTotalPrice() {
        double expectedTotal = monthlyPrice * booking.getDuration();
        assertEquals(expectedTotal, booking.getTotalPrice());

        booking.setDuration(5);
        assertEquals(monthlyPrice * 5, booking.getTotalPrice());

        booking.setDuration(1);
        assertEquals(monthlyPrice, booking.getTotalPrice());
    }

    @Test
    public void testBuilder() {
        Booking builtBooking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .userId(dummyUserId)
                .kosId(dummyKosId)
                .checkInDate(validCheckInDate)
                .duration(2)
                .monthlyPrice(monthlyPrice)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        assertNotNull(builtBooking);
        assertEquals(2, builtBooking.getDuration());
        assertEquals(monthlyPrice * 2, builtBooking.getTotalPrice());
    }

    @Test
    public void testNoArgsConstructor() {
        Booking emptyBooking = new Booking();

        assertNotNull(emptyBooking);
        assertNull(emptyBooking.getBookingId());
        assertNull(emptyBooking.getUserId());
        assertNull(emptyBooking.getKosId());
        assertNull(emptyBooking.getCheckInDate());
        assertEquals(0, emptyBooking.getDuration());
        assertEquals(0.0, emptyBooking.getMonthlyPrice());
        assertNull(emptyBooking.getFullName());
        assertNull(emptyBooking.getPhoneNumber());
        assertNull(emptyBooking.getStatus());
    }

    // ===== CONSTRUCTOR VALIDATION TESTS =====

    @Test
    public void testConstructorWithNullBookingIdShouldGenerateId() {
        Booking bookingWithNullId = new Booking(
                null,
                dummyUserId,
                dummyKosId,
                validCheckInDate,
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        assertNotNull(bookingWithNullId.getBookingId());
    }

    @Test
    public void testConstructorWithNullStatusShouldDefaultToPendingPayment() {
        Booking bookingWithNullStatus = new Booking(
                UUID.randomUUID(),
                dummyUserId,
                dummyKosId,
                validCheckInDate,
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                null
        );

        assertEquals(BookingStatus.PENDING_PAYMENT, bookingWithNullStatus.getStatus());
    }

    @Test
    public void testInvalidDurationZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 0, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Duration must be at least 1 month"));
    }

    @Test
    public void testInvalidDurationNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, -1, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Duration must be at least 1 month"));
    }

    @Test
    public void testInvalidCheckInDateToday() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    LocalDate.now(), 2, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Booking must be made at least 1 day in advance"));
    }

    @Test
    public void testInvalidCheckInDatePast() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    LocalDate.now().minusDays(1), 2, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Booking must be made at least 1 day in advance"));
    }

    @Test
    public void testValidCheckInDate() {
        assertDoesNotThrow(() -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    LocalDate.now().plusDays(1), 2, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });

        assertDoesNotThrow(() -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    LocalDate.now().plusDays(30), 2, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
    }

    @Test
    public void testInvalidMonthlyPriceZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, 0.0, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Monthly price must be greater than 0"));
    }

    @Test
    public void testInvalidMonthlyPriceNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, -1000.0, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Monthly price must be greater than 0"));
    }

    @Test
    public void testInvalidFullNameNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, monthlyPrice, null, phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Full name cannot be empty"));
    }

    @Test
    public void testInvalidFullNameEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, monthlyPrice, "", phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Full name cannot be empty"));
    }

    @Test
    public void testInvalidFullNameWhitespace() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, monthlyPrice, "   ", phoneNumber, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Full name cannot be empty"));
    }

    @Test
    public void testInvalidPhoneNumberNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, monthlyPrice, fullName, null, BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }

    @Test
    public void testInvalidPhoneNumberEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, monthlyPrice, fullName, "", BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }

    @Test
    public void testInvalidPhoneNumberWhitespace() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                    validCheckInDate, 2, monthlyPrice, fullName, "   ", BookingStatus.PENDING_PAYMENT);
        });
        assertTrue(exception.getMessage().contains("Phone number cannot be empty"));
    }

    // ===== SETTERS TESTS =====

    @Test
    public void testSetters() {
        UUID newBookingId = UUID.randomUUID();
        booking.setBookingId(newBookingId);
        assertEquals(newBookingId, booking.getBookingId());

        UUID newUserId = UUID.randomUUID();
        booking.setUserId(newUserId);
        assertEquals(newUserId, booking.getUserId());

        UUID newKosId = UUID.randomUUID();
        booking.setKosId(newKosId);
        assertEquals(newKosId, booking.getKosId());

        LocalDate newDate = LocalDate.now().plusDays(10);
        booking.setCheckInDate(newDate);
        assertEquals(newDate, booking.getCheckInDate());

        booking.setDuration(5);
        assertEquals(5, booking.getDuration());

        booking.setMonthlyPrice(2000000.0);
        assertEquals(2000000.0, booking.getMonthlyPrice());

        booking.setFullName("Jane Smith");
        assertEquals("Jane Smith", booking.getFullName());

        booking.setPhoneNumber("087654321098");
        assertEquals("087654321098", booking.getPhoneNumber());

        booking.setStatus(BookingStatus.APPROVED);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());

        booking.setStatus(null);
        assertNull(booking.getStatus());
    }

    // ===== STATUS TESTS =====

    @Test
    public void testAllStatusValues() {
        for (BookingStatus status : BookingStatus.values()) {
            booking.setStatus(status);
            assertEquals(status, booking.getStatus());
        }
    }

    // ===== EDGE CASES =====

    @Test
    public void testTotalPriceEdgeCases() {
        booking.setDuration(0);
        assertEquals(0.0, booking.getTotalPrice());

        booking.setDuration(-5);
        assertEquals(monthlyPrice * -5, booking.getTotalPrice());

        booking.setMonthlyPrice(1234.567);
        booking.setDuration(3);
        assertEquals(1234.567 * 3, booking.getTotalPrice(), 0.001);
    }

    // ===== EQUALS/HASHCODE TESTS =====

    @Test
    public void testEqualsAndHashCode() {
        // Same object
        assertEquals(booking, booking);

        // Null object
        assertNotEquals(booking, null);

        // Different class
        assertNotEquals(booking, "not a booking");

        // Same ID
        UUID sameId = UUID.randomUUID();
        Booking booking1 = new Booking(sameId, dummyUserId, dummyKosId,
                validCheckInDate, 3, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        Booking booking2 = new Booking(sameId, UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now().plusDays(10), 5, 2000000.0, "Different", "987654321", BookingStatus.PAID);

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());

        // Different IDs
        Booking booking3 = new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                validCheckInDate, 3, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);
        Booking booking4 = new Booking(UUID.randomUUID(), dummyUserId, dummyKosId,
                validCheckInDate, 3, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);

        assertNotEquals(booking3, booking4);

        // Both null IDs
        Booking nullId1 = new Booking();
        Booking nullId2 = new Booking();
        nullId1.setBookingId(null);
        nullId2.setBookingId(null);
        assertEquals(nullId1, nullId2);

        // One null ID
        Booking withNull = new Booking();
        withNull.setBookingId(null);
        assertNotEquals(booking, withNull);
        assertNotEquals(withNull, booking);
    }

    @Test
    public void testHashCodeConsistency() {
        int hash1 = booking.hashCode();
        int hash2 = booking.hashCode();
        assertEquals(hash1, hash2);

        // Hash with null ID (doesn't throw exception)
        Booking nullId = new Booking();
        nullId.setBookingId(null);
        int nullHash1 = nullId.hashCode();
        int nullHash2 = nullId.hashCode();
        assertEquals(nullHash1, nullHash2); // Consistent
    }

    // ===== BUILDER VALIDATION =====

    @Test
    public void testBuilderValidation() {
        // Valid builder
        assertDoesNotThrow(() -> {
            Booking.builder()
                    .bookingId(UUID.randomUUID())
                    .userId(dummyUserId)
                    .kosId(dummyKosId)
                    .checkInDate(validCheckInDate)
                    .duration(3)
                    .monthlyPrice(monthlyPrice)
                    .fullName(fullName)
                    .phoneNumber(phoneNumber)
                    .status(BookingStatus.PENDING_PAYMENT)
                    .build();
        });

        // Invalid date
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Booking.builder()
                    .checkInDate(LocalDate.now())
                    .duration(3)
                    .monthlyPrice(monthlyPrice)
                    .fullName(fullName)
                    .phoneNumber(phoneNumber)
                    .build();
        });
        assertTrue(exception.getMessage().contains("Booking must be made at least 1 day in advance"));
    }

    // ===== JPA ANNOTATIONS =====

    @Test
    public void testJPAAnnotations() {
        assertTrue(Booking.class.isAnnotationPresent(jakarta.persistence.Entity.class));
        assertTrue(Booking.class.isAnnotationPresent(jakarta.persistence.Table.class));
        
        jakarta.persistence.Table table = Booking.class.getAnnotation(jakarta.persistence.Table.class);
        assertEquals("bookings", table.name());
    }
}