package id.ac.ui.cs.advprog.papikosbe.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookingDatabaseIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Test
    public void testAddBookingToDatabase() {
        // Buat objek Booking baru
        Booking booking = new Booking(
                UUID.randomUUID(),         // bookingId (biasanya di-generate dari client atau service)
                UUID.randomUUID(),         // userId
                UUID.randomUUID(),         // kosId (referensi ke entitas Kos)
                LocalDate.now().plusDays(1), // checkInDate: besok
                3,                         // duration (3 bulan)
                BookingStatus.PENDING_PAYMENT // status awal
        );

        // Panggil service untuk membuat (persist) booking ke database
        Booking createdBooking = bookingService.createBooking(booking);

        // Ambil kembali booking dari database menggunakan service
        Optional<Booking> retrievedBooking = bookingService.findBookingById(createdBooking.getBookingId());

        // Pastikan booking ditemukan dan data sesuai
        assertTrue(retrievedBooking.isPresent(), "Booking seharusnya ada di database");
        assertEquals(createdBooking.getBookingId(), retrievedBooking.get().getBookingId(),
                "Booking ID harus sama");
        // Misalnya, cek juga statusnya (atau field lain sesuai kebutuhan)
        assertEquals(BookingStatus.PENDING_PAYMENT, retrievedBooking.get().getStatus(),
                "Status awal booking harus PENDING_PAYMENT");
    }
}
