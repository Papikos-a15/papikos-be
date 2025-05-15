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
    private double monthlyPrice;
    private String fullName;
    private String phoneNumber;
    
    @BeforeEach
    public void setUp() {
        // Mengambil instance service dengan pola Singleton
        bookingService = BookingServiceImpl.getInstance();
        bookingService.clearStore();  // ← reset state sebelum tiap test
        
        // Initialize test data
        monthlyPrice = 1200000.0;
        fullName = "John Doe";
        phoneNumber = "081234567890";
    }

    @Test
    public void testCreateBookingWithPersonalDetails() {
        // Membuat booking baru dengan data diri lengkap
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(7),  // Check-in date 
                3,                            // Duration in months
                monthlyPrice,                 // Monthly price
                fullName,                     // Full name
                phoneNumber,                  // Phone number
                BookingStatus.PENDING_PAYMENT
        );
        
        Booking createdBooking = bookingService.createBooking(booking);
        
        // Verifikasi data diri tersimpan dengan benar
        assertNotNull(createdBooking, "Created booking should not be null");
        assertEquals(fullName, createdBooking.getFullName(), "Full name should be stored correctly");
        assertEquals(phoneNumber, createdBooking.getPhoneNumber(), "Phone number should be stored correctly");
        assertEquals(monthlyPrice, createdBooking.getMonthlyPrice(), "Monthly price should be stored correctly");
        assertEquals(LocalDate.now().plusDays(7), createdBooking.getCheckInDate(), "Check-in date should be stored correctly");
        assertEquals(3, createdBooking.getDuration(), "Duration should be stored correctly");
    }
    
    @Test
    public void testCalculateTotalPrice() {
        // Verifikasi perhitungan total harga (harga bulanan × durasi)
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(7),
                3,                     // 3 months
                monthlyPrice,          // 1,200,000 per month
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );
        
        bookingService.createBooking(booking);
        
        Optional<Booking> retrievedBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(retrievedBooking.isPresent(), "Booking should be found");
        
        double expectedTotal = monthlyPrice * 3; // 3,600,000
        assertEquals(expectedTotal, retrievedBooking.get().getTotalPrice(), 
                "Total price should be monthly price × duration");
    }
    
    @Test
    public void testEditBookingBeforeApproval() {
        // Tenant should be able to edit booking details before approval
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );
        
        bookingService.createBooking(booking);
        
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
        
        // Verify updates
        Optional<Booking> updatedBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(updatedBooking.isPresent(), "Updated booking should be found");
        assertEquals(updatedName, updatedBooking.get().getFullName(), "Name should be updated");
        assertEquals(updatedPhone, updatedBooking.get().getPhoneNumber(), "Phone should be updated");
        assertEquals(updatedCheckIn, updatedBooking.get().getCheckInDate(), "Check-in date should be updated");
        assertEquals(updatedDuration, updatedBooking.get().getDuration(), "Duration should be updated");
    }

    @Test
    public void testEditBookingAfterApprovalShouldFail() {
        // Create booking with PENDING_PAYMENT status
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        // Save initial booking
        Booking createdBooking = bookingService.createBooking(booking);

        // First update: Change status to PAID (this should work)
        bookingService.updateBookingStatus(createdBooking.getBookingId(), BookingStatus.PAID);

        // Verify status changed
        Optional<Booking> paidBooking = bookingService.findBookingById(createdBooking.getBookingId());
        assertTrue(paidBooking.isPresent(), "Booking should exist");
        assertEquals(BookingStatus.PAID, paidBooking.get().getStatus(), "Status should be PAID");

        // Now try to edit other details (should fail)
        Booking updatedBooking = paidBooking.get();
        updatedBooking.setFullName("New Name");
        updatedBooking.setDuration(5);

        // This should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            bookingService.updateBooking(updatedBooking);
        }, "Should not be able to edit booking after approval");
    }
    
    @Test
    public void testCancelBooking() {
        // Simple cancellation test
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );
        
        bookingService.createBooking(booking);
        bookingService.cancelBooking(booking.getBookingId());
        
        Optional<Booking> cancelledBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(cancelledBooking.isPresent(), "Booking should be found after cancellation");
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.get().getStatus(), 
                "Booking status should be CANCELLED");
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
                UUID.randomUUID(), 
                UUID.randomUUID(),
                LocalDate.now().plusDays(1), 
                1, 
                monthlyPrice,
                "User One",
                "081111111111",
                BookingStatus.PENDING_PAYMENT
        );
        
        Booking b2 = new Booking(
                UUID.randomUUID(), 
                UUID.randomUUID(), 
                UUID.randomUUID(),
                LocalDate.now().plusDays(2), 
                2, 
                monthlyPrice + 300000,
                "User Two",
                "082222222222",
                BookingStatus.PENDING_PAYMENT
        );

        bookingService.createBooking(b1);
        bookingService.createBooking(b2);

        List<Booking> all = bookingService.findAllBookings();
        assertEquals(2, all.size(), "Seharusnya ada 2 booking total");
        assertTrue(all.contains(b1), "Harus mengandung booking pertama");
        assertTrue(all.contains(b2), "Harus mengandung booking kedua");
    }
}