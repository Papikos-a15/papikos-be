package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class BookingRepository {
    // Instance untuk pola Singleton
    private static BookingRepository instance;

    // Data store sementara (dummy store)
    private Map<UUID, Booking> bookingStore;

    // Constructor private untuk Singleton
    private BookingRepository() {
        // TODO: Inisialisasi bookingStore
    }

    // Method untuk mengembalikan instance singleton
    public static synchronized BookingRepository getInstance() {
        // TODO: Inisialisasi instance jika belum ada
        return instance;
    }

    // Method untuk menyimpan booking, (dummy return null)
    public Booking save(Booking booking) {
        // TODO: Implementasikan penyimpanan booking ke dalam bookingStore
        return null;
    }

    // Method untuk mencari booking berdasarkan bookingId, (dummy return Optional.empty())
    public Optional<Booking> findById(UUID bookingId) {
        // TODO: Implementasikan pencarian booking berdasarkan bookingId
        return Optional.empty();
    }

    // Method untuk mencari booking berdasarkan userId, (dummy return list kosong)
    public List<Booking> findByUserId(UUID userId) {
        // TODO: Implementasikan pencarian booking berdasarkan userId
        return new ArrayList<>();
    }

    // Method untuk menghapus booking berdasarkan bookingId
    public void deleteById(UUID bookingId) {
        // TODO: Implementasikan penghapusan booking dari bookingStore
    }

    // Metode tambahan untuk keperluan testing: Membersihkan bookingStore
    public void clearStore() {
        // TODO: Implementasikan pembersihan semua data di bookingStore
    }
}
