package id.ac.ui.cs.advprog.papikosbe.service.booking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

public class BookingServiceImplTest {

    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        // Mengambil instance service dengan pola Singleton
        bookingService = BookingServiceImpl.getInstance();
        bookingService.clearStore();  // ← reset state sebelum tiap test
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

    @Test
    public void testFindAllBookings() {
        Booking b1 = new Booking(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now().plusDays(1), 1, BookingStatus.PENDING_PAYMENT);
        Booking b2 = new Booking(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now().plusDays(2), 2, BookingStatus.PENDING_PAYMENT);

        bookingService.createBooking(b1);
        bookingService.createBooking(b2);

        List<Booking> all = bookingService.findAllBookings();
        assertEquals(2, all.size(), "Seharusnya ada 2 booking total");
        assertTrue(all.contains(b1), "Harus mengandung booking pertama");
        assertTrue(all.contains(b2), "Harus mengandung booking kedua");
    }
}
