package id.ac.ui.cs.advprog.papikosbe.service.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.math.BigDecimal;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private KosService kosService;

    @Mock
    private PaymentService paymentService;

    private BookingService bookingService;
    private double monthlyPrice;
    private String fullName;
    private String phoneNumber;
    private UUID ownerId;
    private UUID kosId;
    private Kos testKos;
    private UUID userId;
    @BeforeEach
    public void setUp() {
        // Create a real BookingServiceImpl with mocked dependencies
        BookingServiceImpl realService = new BookingServiceImpl(kosService, paymentService);

        // Set up the static instance reference to our test instance
        bookingService = realService;
        ((BookingServiceImpl)bookingService).clearStore();

        // Initialize test data
        monthlyPrice = 1200000.0;
        fullName = "John Doe";
        phoneNumber = "081234567890";

        // Create a test Kos object with known owner
        ownerId = UUID.randomUUID();
        kosId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testKos = new Kos();
        testKos.setId(kosId);
        testKos.setOwnerId(ownerId);
        testKos.setName("Test Kos");
        testKos.setPrice(monthlyPrice);
        testKos.setAddress("Test Address");
        testKos.setAvailable(true);

        // Fix unnecessary stubbing error with lenient()
        lenient().when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
    }

    @Test
    public void testCreateBookingWithPersonalDetails() {
        // Create booking with test kos
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        Booking createdBooking = bookingService.createBooking(booking);

        // Verify kos service was called
        verify(kosService).getKosById(kosId);

        // Verify booking details
        assertNotNull(createdBooking);
        assertEquals(fullName, createdBooking.getFullName());
        assertEquals(phoneNumber, createdBooking.getPhoneNumber());
        assertEquals(monthlyPrice, createdBooking.getMonthlyPrice());
        assertEquals(LocalDate.now().plusDays(7), createdBooking.getCheckInDate());
        assertEquals(3, createdBooking.getDuration());
    }

    @Test
    public void testCalculateTotalPrice() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        bookingService.createBooking(booking);

        verify(kosService).getKosById(kosId);

        Optional<Booking> retrievedBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(retrievedBooking.isPresent());

        double expectedTotal = monthlyPrice * 3;
        assertEquals(expectedTotal, retrievedBooking.get().getTotalPrice());
    }

    @Test
    public void testEditBookingBeforeApproval() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        // Edit booking details
        String updatedName = "Jane Doe";
        String updatedPhone = "089876543210";
        LocalDate updatedCheckIn = LocalDate.now().plusDays(14);
        int updatedDuration = 6;

        booking.setFullName(updatedName);
        booking.setPhoneNumber(updatedPhone);
        booking.setCheckInDate(updatedCheckIn);
        booking.setDuration(updatedDuration);

        bookingService.updateBooking(booking);

        Optional<Booking> updatedBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(updatedBooking.isPresent());
        assertEquals(updatedName, updatedBooking.get().getFullName());
        assertEquals(updatedPhone, updatedBooking.get().getPhoneNumber());
        assertEquals(updatedCheckIn, updatedBooking.get().getCheckInDate());
        assertEquals(updatedDuration, updatedBooking.get().getDuration());
    }

    @Test
    public void testEditBookingAfterPaymentBeforeApproval() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        Booking createdBooking = bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        // Instead of doNothing(), use when().thenReturn() for non-void method
        // Assuming createPayment returns something, let's return null for simplicity
        when(paymentService.createPayment(any(), any(), any())).thenReturn(null);

        bookingService.payBooking(createdBooking.getBookingId());
        verify(kosService, times(2)).getKosById(kosId);
        verify(paymentService).createPayment(eq(userId), eq(ownerId), any(BigDecimal.class));

        Optional<Booking> paidBooking = bookingService.findBookingById(createdBooking.getBookingId());
        assertTrue(paidBooking.isPresent());
        assertEquals(BookingStatus.PAID, paidBooking.get().getStatus());

        // Edit after payment
        Booking updatedBooking = paidBooking.get();
        updatedBooking.setFullName("New Name After Payment");
        updatedBooking.setDuration(5);

        assertDoesNotThrow(() -> bookingService.updateBooking(updatedBooking));

        Optional<Booking> finalBooking = bookingService.findBookingById(createdBooking.getBookingId());
        assertTrue(finalBooking.isPresent());
        assertEquals("New Name After Payment", finalBooking.get().getFullName());
        assertEquals(5, finalBooking.get().getDuration());
    }

    @Test
    public void testEditBookingAfterApprovalShouldFail() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        Booking createdBooking = bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        // Fix the doNothing() call
        when(paymentService.createPayment(any(), any(), any())).thenReturn(null);

        bookingService.payBooking(createdBooking.getBookingId());
        verify(paymentService).createPayment(eq(userId), eq(ownerId), any(BigDecimal.class));

        bookingService.approveBooking(createdBooking.getBookingId());

        Optional<Booking> approvedBooking = bookingService.findBookingById(createdBooking.getBookingId());
        assertTrue(approvedBooking.isPresent());
        assertEquals(BookingStatus.APPROVED, approvedBooking.get().getStatus());

        Booking updatedBooking = approvedBooking.get();
        updatedBooking.setFullName("New Name");
        updatedBooking.setDuration(5);

        assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(updatedBooking));
    }

    @Test
    public void testPayBookingFailsIfAlreadyPaid() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        Booking createdBooking = bookingService.createBooking(booking);
        // Fix the doNothing() call
        when(paymentService.createPayment(any(), any(), any())).thenReturn(null);

        bookingService.payBooking(createdBooking.getBookingId());
        verify(paymentService).createPayment(eq(userId), eq(ownerId), any(BigDecimal.class));

        assertThrows(IllegalStateException.class, () -> bookingService.payBooking(createdBooking.getBookingId()));
    }

    @Test
    public void testApproveBookingFailsIfNotPaid() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        Booking createdBooking = bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        assertThrows(IllegalStateException.class, () -> bookingService.approveBooking(createdBooking.getBookingId()));
    }

    @Test
    public void testCancelBooking() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        bookingService.cancelBooking(booking.getBookingId());

        Optional<Booking> cancelledBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(cancelledBooking.isPresent());
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.get().getStatus());
    }

    @Test
    public void testFindBookingByIdNotFound() {
        Optional<Booking> result = bookingService.findBookingById(UUID.randomUUID());
        assertFalse(result.isPresent(), "Booking should not be found for an unknown id");
    }

    @Test
    public void testFindAllBookings() {
        Booking b1 = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(1),
                1,
                monthlyPrice,
                "User One",
                "081111111111",
                BookingStatus.PENDING_PAYMENT
        );

        Booking b2 = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(2),
                2,
                monthlyPrice + 300000,
                "User Two",
                "082222222222",
                BookingStatus.PENDING_PAYMENT
        );

        bookingService.createBooking(b1);
        bookingService.createBooking(b2);
        verify(kosService, times(2)).getKosById(kosId);

        List<Booking> all = bookingService.findAllBookings();
        assertEquals(2, all.size());
        assertTrue(all.contains(b1));
        assertTrue(all.contains(b2));
    }

}