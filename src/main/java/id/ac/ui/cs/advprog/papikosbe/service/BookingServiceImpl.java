package id.ac.ui.cs.advprog.papikosbe.service;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingServiceImpl implements BookingService {

    private static BookingServiceImpl instance;
    private Map<UUID, Booking> bookingStore;

    // Private constructor dengan inisialisasi bookingStore
    private BookingServiceImpl() {
        bookingStore = new ConcurrentHashMap<>();
    }

    public static synchronized BookingServiceImpl getInstance() {
        if (instance == null) {
            instance = new BookingServiceImpl();
        }
        return instance;
    }

    @Override
    public Booking createBooking(Booking booking) {
        // Simpan booking ke dalam store
        bookingStore.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking != null) {
            booking.setStatus(BookingStatus.CANCELLED);
        }
    }
}
