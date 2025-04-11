package id.ac.ui.cs.advprog.papikosbe.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

public class BookingServiceImplTest {

    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        // Mengambil instance service dengan pola Singleton
        bookingService = BookingServiceImpl.getInstance();
    }

    @Test
    public void testCreateBooking() {
        // Membuat booking baru dengan status awal PENDING_PAYMENT
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                3,
                BookingStatus.PENDING_PAYMENT
        );
        Booking createdBooking = bookingService.createBooking(booking);
        // Diharapkan booking yang dibuat tidak null, memiliki ID, dan status PENDING_PAYMENT
        assertNotNull(createdBooking, "Created booking should not be null");
        assertNotNull(createdBooking.getBookingId(), "Booking id should not be null");
        assertEquals(BookingStatus.PENDING_PAYMENT, createdBooking.getStatus(), "Initial status should be PENDING_PAYMENT");
    }

    @Test
    public void testFindBookingByIdNotFound() {
        // Mencari booking dengan ID acak, seharusnya Optional.empty()
        Optional<Booking> result = bookingService.findBookingById(UUID.randomUUID());
        assertFalse(result.isPresent(), "Booking should not be found for an unknown id");
    }

    @Test
    public void testCancelBooking() {
        // Membuat dan menyimpan booking, lalu batalkan
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                3,
                BookingStatus.PENDING_PAYMENT
        );
        bookingService.createBooking(booking);
        bookingService.cancelBooking(booking.getBookingId());
        Optional<Booking> cancelledBooking = bookingService.findBookingById(booking.getBookingId());
        // Setelah cancel, status harus berubah menjadi CANCELLED
        assertTrue(cancelledBooking.isPresent(), "Booking should be found after cancellation");
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.get().getStatus(), "Booking status should be CANCELLED after cancellation");
    }
}
