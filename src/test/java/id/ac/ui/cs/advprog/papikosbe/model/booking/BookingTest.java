package id.ac.ui.cs.advprog.papikosbe.model.booking;
import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
public class BookingTest {

    private Booking booking;
    private UUID dummyUserId;
    private UUID dummyKosId;
    
    @BeforeEach
    public void setUp() {
        dummyUserId = UUID.randomUUID();
        dummyKosId = UUID.randomUUID();
        // Pastikan checkInDate adalah minimal besok
        LocalDate checkIn = LocalDate.now().plusDays(1);
        
        // Pembuatan objek Booking dengan status awal PENDING_PAYMENT
        booking = new Booking(
            UUID.randomUUID(), 
            dummyUserId,
            dummyKosId, 
            checkIn, 
            3,        // durasi 3 bulan 
            BookingStatus.PENDING_PAYMENT
        );
    }
    
    @Test
    public void testBookingInitialization() {
        // Memeriksa bahwa semua field diinisialisasi dengan benar
        assertNotNull(booking.getBookingId());
        assertEquals(dummyUserId, booking.getUserId());
        assertEquals(dummyKosId, booking.getKosId());
        // checkInDate harus setidaknya sama dengan atau setelah hari ini
        assertTrue(booking.getCheckInDate().isAfter(LocalDate.now()) || booking.getCheckInDate().isEqual(LocalDate.now()));
        assertTrue(booking.getDuration() >= 1);
        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
    }
    
    @Test
    public void testCalculateTotalPrice() {
        // Menguji perhitungan total harga berdasarkan harga bulanan (misal: 1.000.000 per bulan)
        double monthlyPrice = 1000000.0;
        double expectedTotal = monthlyPrice * booking.getDuration();
        assertEquals(expectedTotal, booking.calculateTotalPrice(monthlyPrice));
    }
    
    @Test
    public void testInvalidDuration() {
        // Pembuatan booking dengan durasi kurang dari 1 bulan harus mengeluarkan Exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId, LocalDate.now().plusDays(1), 0, BookingStatus.PENDING_PAYMENT);
        });
        String expectedMessage = "Duration must be at least 1 month";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testInvalidCheckInDate() {
        // Check-in date sebelum hari ini harus mengeluarkan Exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId, LocalDate.now().minusDays(1), 2, BookingStatus.PENDING_PAYMENT);
        });
        String expectedMessage = "Check-in date cannot be in the past";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    // Test tambahan untuk transisi status
    @Test
    public void testStatusTransitionToPaid() {
        // Simulasi perubahan status booking dari PENDING_PAYMENT ke PAID
        booking.setStatus(BookingStatus.PAID);
        assertEquals(BookingStatus.PAID, booking.getStatus());
    }

    @Test
    public void testStatusTransitionToActive() {
        // Simulasi perubahan status booking dari PAID ke ACTIVE
        booking.setStatus(BookingStatus.PAID);
        booking.setStatus(BookingStatus.ACTIVE);
        assertEquals(BookingStatus.ACTIVE, booking.getStatus());
    }
    
    @Test
    public void testStatusTransitionToCancelled() {
        // Simulasi pembatalan booking
        booking.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }
}
