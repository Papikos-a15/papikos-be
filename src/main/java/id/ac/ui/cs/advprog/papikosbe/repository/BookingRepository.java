package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class BookingRepository {
    private static BookingRepository instance;
    private Map<UUID, Booking> bookingStore;

    // Konstruktor private, inisialisasi bookingStore
    private BookingRepository() {
        bookingStore = new HashMap<>();
    }

    // Implementasi pola Singleton
    public static synchronized BookingRepository getInstance() {
        if (instance == null) {
            instance = new BookingRepository();
        }
        return instance;
    }

    // Menyimpan booking ke dalam store dan mengembalikan objek yang sama
    public Booking save(Booking booking) {
        bookingStore.put(booking.getBookingId(), booking);
        return booking;
    }

    // Mencari booking berdasarkan bookingId
    public Optional<Booking> findById(UUID bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    // Mencari dan mengembalikan daftar booking berdasarkan userId
    public List<Booking> findByUserId(UUID userId) {
        List<Booking> results = new ArrayList<>();
        for (Booking booking : bookingStore.values()) {
            if (booking.getUserId().equals(userId)) {
                results.add(booking);
            }
        }
        return results;
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookingStore.values());
    }
    
    // Menghapus booking berdasarkan bookingId
    public void deleteById(UUID bookingId) {
        bookingStore.remove(bookingId);
    }

    // Membersihkan semua data di bookingStore untuk keperluan pengujian
    public void clearStore() {
        bookingStore.clear();
    }
}
