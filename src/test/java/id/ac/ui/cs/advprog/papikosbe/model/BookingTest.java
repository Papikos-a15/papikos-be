import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;

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
        
        // Pembuatan objek Booking (menggunakan skeleton model yang belum lengkap)
        booking = new Booking(
            UUID.randomUUID(), 
            dummyUserId,
            dummyKosId, 
            checkIn, 
            3,        // durasi 3 bulan 
            BookingStatus.WAITING
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
        assertEquals(BookingStatus.WAITING, booking.getStatus());
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
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId, LocalDate.now().plusDays(1), 0, BookingStatus.WAITING);
        });
        String expectedMessage = "Duration must be at least 1 month";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testInvalidCheckInDate() {
        // Check-in date sebelum hari ini harus mengeluarkan Exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Booking(UUID.randomUUID(), dummyUserId, dummyKosId, LocalDate.now().minusDays(1), 2, BookingStatus.WAITING);
        });
        String expectedMessage = "Check-in date cannot be in the past";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testBookingRepositorySingleton() {
        // Memastikan bahwa repository booking menerapkan design pattern Singleton
        BookingRepository instanceOne = BookingRepository.getInstance();
        BookingRepository instanceTwo = BookingRepository.getInstance();
        assertSame(instanceOne, instanceTwo, "BookingRepository harus menggunakan singleton");
    }
}
