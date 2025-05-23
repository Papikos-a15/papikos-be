package id.ac.ui.cs.advprog.papikosbe.service.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private KosService kosService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingValidator stateValidator;

    @Mock
    private BookingAccessValidator bookingAccessValidator;

    private BookingService bookingService;
    private double monthlyPrice;
    private String fullName;
    private String phoneNumber;
    private UUID ownerId;
    private UUID kosId;
    private Kos testKos;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, kosService, stateValidator, bookingAccessValidator, transactionService);

        monthlyPrice = 1500000.0;
        fullName = "John Doe";
        phoneNumber = "081234567890";
        ownerId = UUID.randomUUID();
        kosId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // Setup test Kos with all required fields
        testKos = new Kos();
        testKos.setId(kosId);
        testKos.setOwnerId(ownerId);
        testKos.setName("Test Kos");
        testKos.setAddress("Test Address");
        testKos.setDescription("Test Description");
        testKos.setPrice(monthlyPrice);
        testKos.setMaxCapacity(10);
        testKos.setAvailableRooms(5); // Set available rooms
        testKos.setAvailable(true);   // Set as available
}

    @Test
    public void testCreateBookingWithPersonalDetails() {
        // Create booking with test kos
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createdBooking = bookingService.createBooking(booking);

        // Verify kos service was called
        verify(kosService).getKosById(kosId);
        verify(bookingRepository).save(any(Booking.class));

        // Verify booking details
        assertNotNull(createdBooking);
        assertEquals(fullName, createdBooking.getFullName());
        assertEquals(phoneNumber, createdBooking.getPhoneNumber());
        assertEquals(monthlyPrice, createdBooking.getMonthlyPrice());
        assertEquals(LocalDate.now().plusDays(7), createdBooking.getCheckInDate());
        assertEquals(3, createdBooking.getDuration());
    }

    @Test
    public void testCalculateTotalPrice() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));

        bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        Optional<Booking> retrievedBooking = bookingService.findBookingById(booking.getBookingId());
        assertTrue(retrievedBooking.isPresent());

        double expectedTotal = monthlyPrice * 3;
        assertEquals(expectedTotal, retrievedBooking.get().getTotalPrice());
    }

    @Test
    public void testEditBookingBeforeApproval() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));

        bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        // Edit booking details
        String updatedName = "Jane Doe";
        String updatedPhone = "089876543210";
        LocalDate updatedCheckIn = LocalDate.now().plusDays(14);
        int updatedDuration = 6;

        booking.setFullName(updatedName);
        booking.setPhoneNumber(updatedPhone);
        booking.setCheckInDate(updatedCheckIn);
        booking.setDuration(updatedDuration);

        // Update the mock to return the updated booking
        Booking updatedBookingObj = new Booking(
                booking.getBookingId(), booking.getUserId(), booking.getKosId(),
                updatedCheckIn, updatedDuration, booking.getMonthlyPrice(),
                updatedName, updatedPhone, booking.getStatus()
        );
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(updatedBookingObj));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBookingObj);

        bookingService.updateBooking(booking);
        verify(bookingRepository, times(2)).save(any(Booking.class));

        Optional<Booking> result = bookingService.findBookingById(booking.getBookingId());
        assertTrue(result.isPresent());
        assertEquals(updatedName, result.get().getFullName());
        assertEquals(updatedPhone, result.get().getPhoneNumber());
        assertEquals(updatedCheckIn, result.get().getCheckInDate());
        assertEquals(updatedDuration, result.get().getDuration());
    }

    @Test
    public void testEditBookingAfterPaymentBeforeApproval() throws Exception {
        // Create booking with test kos
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        // Basic setup - kos and initial booking state
        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));

        // Create booking
        Booking createdBooking = bookingService.createBooking(booking);

        // Setup for the paid state AFTER initial call
        Booking paidBooking = new Booking(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getKosId(),
                booking.getCheckInDate(),
                booking.getDuration(),
                booking.getMonthlyPrice(),
                booking.getFullName(),
                booking.getPhoneNumber(),
                BookingStatus.PAID
        );

        // After payment, return the paid booking
        doAnswer(invocation -> {
            // Update repository mock to return paid booking after this call
            when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(paidBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(paidBooking);
            return null;
        }).when(transactionService).createPayment(any(UUID.class), any(UUID.class), any(BigDecimal.class));

        // Now pay the booking - this transitions from PENDING to PAID
        bookingService.payBooking(createdBooking.getBookingId());

        // Edit after payment
        Booking updatedBooking = new Booking(
                paidBooking.getBookingId(),
                paidBooking.getUserId(),
                paidBooking.getKosId(),
                paidBooking.getCheckInDate(),
                5,
                paidBooking.getMonthlyPrice(),
                "New Name After Payment",
                paidBooking.getPhoneNumber(),
                paidBooking.getStatus()
        );

        when(bookingRepository.findById(paidBooking.getBookingId())).thenReturn(Optional.of(paidBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        bookingService.updateBooking(updatedBooking);

        when(bookingRepository.findById(createdBooking.getBookingId())).thenReturn(Optional.of(updatedBooking));

        Optional<Booking> finalBooking = bookingService.findBookingById(createdBooking.getBookingId());
        assertTrue(finalBooking.isPresent());
        assertEquals("New Name After Payment", finalBooking.get().getFullName());
        assertEquals(5, finalBooking.get().getDuration());
    }

    @Test
    public void testEditBookingAfterApprovalShouldFail() throws Exception {
        // Create booking with test kos
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        // Basic setup
        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));

        // Create booking
        Booking createdBooking = bookingService.createBooking(booking);

        // Prepare for payment
        Booking paidBooking = new Booking(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getKosId(),
                booking.getCheckInDate(),
                booking.getDuration(),
                booking.getMonthlyPrice(),
                booking.getFullName(),
                booking.getPhoneNumber(),
                BookingStatus.PAID
        );

        // Update mock after payment
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(paidBooking));

        // Pay the booking
        bookingService.payBooking(createdBooking.getBookingId());

        // Prepare for approval
        Booking approvedBooking = new Booking(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getKosId(),
                booking.getCheckInDate(),
                booking.getDuration(),
                booking.getMonthlyPrice(),
                booking.getFullName(),
                booking.getPhoneNumber(),
                BookingStatus.APPROVED
        );

        // Update mock for approval
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(approvedBooking));

        // Approve the booking
        bookingService.approveBooking(createdBooking.getBookingId());

        // Try to edit the approved booking
        Booking updatedBooking = new Booking(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getKosId(),
                booking.getCheckInDate(),
                booking.getDuration() + 1,
                booking.getMonthlyPrice(),
                "Updated Name",
                booking.getPhoneNumber(),
                BookingStatus.APPROVED
        );

        // This is the key fix: mock the validator to throw exception when validating an approved booking
        doThrow(new IllegalStateException("Cannot edit booking after it has been approved or cancelled"))
                .when(stateValidator).validateForUpdate(approvedBooking);

        when(bookingRepository.findById(approvedBooking.getBookingId())).thenReturn(Optional.of(approvedBooking));

        assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(updatedBooking));
    }

    @Test
    public void testApproveBooking() {
        // Create paid booking
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PAID
        );

        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));

        // Set up capture for the saved booking
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        // Approve booking
        bookingService.approveBooking(booking.getBookingId());

        // Verify booking was updated and saved with correct status
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.APPROVED, bookingCaptor.getValue().getStatus());
    }

    @Test
    public void testFindBookingsByOwnerId() {
        // Create test kos owned by our test owner
        Kos kos1 = new Kos();
        kos1.setId(UUID.randomUUID());
        kos1.setOwnerId(ownerId);

        Kos kos2 = new Kos();
        kos2.setId(UUID.randomUUID());
        kos2.setOwnerId(ownerId);

        Kos kos3 = new Kos();
        kos3.setId(UUID.randomUUID());
        kos3.setOwnerId(UUID.randomUUID()); // Different owner

        List<Kos> allKosList = List.of(kos1, kos2, kos3);
        when(kosService.getAllKos()).thenReturn(allKosList);

        // Create bookings for these kos
        Booking booking1 = new Booking(UUID.randomUUID(), userId, kos1.getId(),
                LocalDate.now().plusDays(7), 3, monthlyPrice, fullName, phoneNumber,
                BookingStatus.PENDING_PAYMENT);

        Booking booking2 = new Booking(UUID.randomUUID(), userId, kos2.getId(),
                LocalDate.now().plusDays(14), 2, monthlyPrice, fullName, phoneNumber,
                BookingStatus.PAID);

        Booking booking3 = new Booking(UUID.randomUUID(), userId, kos3.getId(),
                LocalDate.now().plusDays(21), 1, monthlyPrice, fullName, phoneNumber,
                BookingStatus.APPROVED);

        List<Booking> allBookings = List.of(booking1, booking2, booking3);
        when(bookingRepository.findAll()).thenReturn(allBookings);

        // Call the method
        List<Booking> ownerBookings = bookingService.findBookingsByOwnerId(ownerId);

        // Verify results
        assertEquals(2, ownerBookings.size());
        assertTrue(ownerBookings.contains(booking1));
        assertTrue(ownerBookings.contains(booking2));
        assertFalse(ownerBookings.contains(booking3));
    }

    @Test
    public void testPayBooking() throws Exception {
        // Create a booking
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));
        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));

        // Pay the booking
        bookingService.payBooking(booking.getBookingId());

        // Verify payment was created with correct parameters
        verify(transactionService).createPayment(any(UUID.class), any(UUID.class), any(BigDecimal.class));

        // Verify booking was updated
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.PAID, bookingCaptor.getValue().getStatus());
    }

    @Test
    public void testCancelBooking() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(7),
                3,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));

        bookingService.createBooking(booking);
        verify(kosService).getKosById(kosId);

        // Create a cancelled version of the booking
        Booking cancelledBooking = new Booking(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getKosId(),
                booking.getCheckInDate(),
                booking.getDuration(),
                booking.getMonthlyPrice(),
                booking.getFullName(),
                booking.getPhoneNumber(),
                BookingStatus.CANCELLED
        );

        // Update mock to return cancelled booking after cancel operation
        doAnswer(invocation -> {
            when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(cancelledBooking));
            return cancelledBooking;
        }).when(bookingRepository).save(any(Booking.class));

        bookingService.cancelBooking(booking.getBookingId());

        Optional<Booking> result = bookingService.findBookingById(booking.getBookingId());
        assertTrue(result.isPresent());
        assertEquals(BookingStatus.CANCELLED, result.get().getStatus());
    }

    @Test
    public void testFindBookingByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(bookingRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<Booking> result = bookingService.findBookingById(randomId);
        assertFalse(result.isPresent(), "Booking should not be found for an unknown id");
    }

    @Test
    public void testFindAllBookings() {
        Booking b1 = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(1),
                1,
                monthlyPrice,
                "User One",
                "081111111111",
                BookingStatus.PENDING_PAYMENT
        );

        Booking b2 = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(2),
                2,
                monthlyPrice + 300000,
                "User Two",
                "082222222222",
                BookingStatus.PENDING_PAYMENT
        );

        when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));
        when(bookingRepository.save(b1)).thenReturn(b1);
        when(bookingRepository.save(b2)).thenReturn(b2);

        bookingService.createBooking(b1);
        bookingService.createBooking(b2);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(b1);
        bookingList.add(b2);

        when(bookingRepository.findAll()).thenReturn(bookingList);

        List<Booking> all = bookingService.findAllBookings();
        assertEquals(2, all.size());
        assertTrue(all.contains(b1));
        assertTrue(all.contains(b2));
        verify(bookingRepository).findAll();
    }

    @Test
    public void testClearStore() {
        // Test the clearStore method
        bookingService.clearStore();

        // Verify that deleteAll was called on the repository
        verify(bookingRepository).deleteAll();
    }

    @Test
    public void testFindBookingsByUserId() {
        // Create bookings for our test user
        Booking userBooking1 = new Booking(
                UUID.randomUUID(),
                userId,
                kosId,
                LocalDate.now().plusDays(1),
                2,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        // Create booking for different user
        UUID otherUserId = UUID.randomUUID();
        Booking otherUserBooking = new Booking(
                UUID.randomUUID(),
                otherUserId,
                kosId,
                LocalDate.now().plusDays(1),
                2,
                monthlyPrice,
                "Other User",
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        List<Booking> allBookings = List.of(userBooking1, otherUserBooking);
        when(bookingRepository.findAll()).thenReturn(allBookings);

        // Call method
        List<Booking> userBookings = bookingService.findBookingsByUserId(userId);

        // Verify
        assertEquals(1, userBookings.size());
        assertTrue(userBookings.contains(userBooking1));
        assertFalse(userBookings.contains(otherUserBooking));
    }

}