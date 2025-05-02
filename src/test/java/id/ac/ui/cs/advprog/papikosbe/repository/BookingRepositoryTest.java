package id.ac.ui.cs.advprog.papikosbe.repository;

import static org.junit.jupiter.api.Assertions.*;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BookingRepositoryTest {

    private BookingRepository repository;
    private Booking sampleBooking;
    private UUID dummyUserId;
    private UUID dummyKosId;

    @BeforeEach
    public void setUp() {
        // Pastikan setiap test dijalankan dengan instance fresh dari repository
        // (meskipun repository adalah singleton, kita bisa mengkosongkannya di awal setiap test)
        repository = BookingRepository.getInstance();
        // Reset repository dengan pembersihan internal jika perlu
        repository.clearStore();

        dummyUserId = UUID.randomUUID();
        dummyKosId = UUID.randomUUID();
        sampleBooking = new Booking(
                UUID.randomUUID(),
                dummyUserId,
                dummyKosId,
                LocalDate.now().plusDays(1),
                3, // durasi 3 bulan
                BookingStatus.PENDING_PAYMENT
        );
    }

    @Test
    public void testSingletonInstance() {
        BookingRepository instanceOne = BookingRepository.getInstance();
        BookingRepository instanceTwo = BookingRepository.getInstance();
        assertSame(instanceOne, instanceTwo, "BookingRepository harus menggunakan pola singleton");
    }

    @Test
    public void testSaveAndFindById() {
        // Simpan booking ke repository
        repository.save(sampleBooking);
        Optional<Booking> retrieved = repository.findById(sampleBooking.getBookingId());
        assertTrue(retrieved.isPresent(), "Booking seharusnya ada di repository setelah disimpan");
        assertEquals(sampleBooking.getBookingId(), retrieved.get().getBookingId());
    }

    @Test
    public void testFindByIdReturnsEmptyIfNotFound() {
        Optional<Booking> retrieved = repository.findById(UUID.randomUUID());
        assertFalse(retrieved.isPresent(), "Booking seharusnya tidak ditemukan jika ID tidak valid");
    }

    @Test
    public void testFindByUserId() {
        // Simulasikan penyimpanan beberapa booking dengan user yang sama
        Booking booking1 = new Booking(
                UUID.randomUUID(), dummyUserId, dummyKosId,
                LocalDate.now().plusDays(1), 3, BookingStatus.PENDING_PAYMENT);
        Booking booking2 = new Booking(
                UUID.randomUUID(), dummyUserId, dummyKosId,
                LocalDate.now().plusDays(2), 2, BookingStatus.PENDING_PAYMENT);
        Booking booking3 = new Booking(
                UUID.randomUUID(), UUID.randomUUID(), dummyKosId,
                LocalDate.now().plusDays(3), 4, BookingStatus.PENDING_PAYMENT);

        repository.save(booking1);
        repository.save(booking2);
        repository.save(booking3);

        List<Booking> userBookings = repository.findByUserId(dummyUserId);
        assertEquals(2, userBookings.size(), "Seharusnya mengembalikan 2 booking untuk user yang sama");
        // Pastikan kedua booking tersebut memang punya dummyUserId
        for (Booking booking : userBookings) {
            assertEquals(dummyUserId, booking.getUserId());
        }
    }

    @Test
    public void testDeleteById() {
        // Simpan booking terlebih dahulu
        repository.save(sampleBooking);
        // Pastikan booking ada
        assertTrue(repository.findById(sampleBooking.getBookingId()).isPresent());
        // Hapus booking
        repository.deleteById(sampleBooking.getBookingId());
        // Pastikan booking sudah tidak ada
        assertFalse(repository.findById(sampleBooking.getBookingId()).isPresent());
    }
}
