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
    import java.util.concurrent.CompletableFuture;
    import java.util.concurrent.ExecutionException;
    
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
            bookingService = new BookingServiceImpl(bookingRepository, kosService,transactionService, stateValidator);
    
            monthlyPrice = 1500000.0;
            fullName = "John Doe";
            phoneNumber = "081234567890";
            ownerId = UUID.randomUUID();
            kosId = UUID.randomUUID();
            userId = UUID.randomUUID();
    
            // Setup test Kos with all required fields
            testKos = Kos.builder()
            .id(kosId)
            .ownerId(ownerId)
            .name("Test Kos")
            .address("Test Address")
            .description("Test Description")
            .price(monthlyPrice)
            .maxCapacity(10)
            .build();
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
        public void testCalculateTotalPrice() throws ExecutionException, InterruptedException {
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

            // Fix: Handle async call properly
            CompletableFuture<Optional<Booking>> result = bookingService.findBookingById(booking.getBookingId());
            Optional<Booking> retrievedBooking = result.get(); // Now properly declared with throws
            assertTrue(retrievedBooking.isPresent());

            double expectedTotal = monthlyPrice * 3;
            assertEquals(expectedTotal, retrievedBooking.get().getTotalPrice());
        }

        @Test
        public void testEditBookingBeforeApproval() throws ExecutionException, InterruptedException {
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

            // Fix: Handle async call properly
            CompletableFuture<Optional<Booking>> result = bookingService.findBookingById(booking.getBookingId());
            Optional<Booking> retrievedBooking = result.get(); // Now properly declared with throws
            assertTrue(retrievedBooking.isPresent());
            assertEquals(updatedName, retrievedBooking.get().getFullName());
            assertEquals(updatedPhone, retrievedBooking.get().getPhoneNumber());
            assertEquals(updatedCheckIn, retrievedBooking.get().getCheckInDate());
            assertEquals(updatedDuration, retrievedBooking.get().getDuration());
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

            CompletableFuture<Optional<Booking>> finalBookingFuture = bookingService.findBookingById(createdBooking.getBookingId());
            Optional<Booking> finalBooking = finalBookingFuture.get();
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
        public void testFindBookingsByOwnerId() throws ExecutionException, InterruptedException {
            // Create test kos owned by our test owner
            Kos kos1 = Kos.builder()
            .id(UUID.randomUUID())
            .ownerId(ownerId)
            .name("Kos 1")
            .address("Address 1")
            .description("Description 1")
            .price(1200000.0)
            .maxCapacity(5)
            .build();
    
            Kos kos2 = Kos.builder()
            .id(UUID.randomUUID())
            .ownerId(ownerId)
            .name("Kos 2")
            .address("Address 2")
            .description("Description 2")
            .price(1500000.0)
            .maxCapacity(8)
            .build();
    
            Kos kos3 = Kos.builder()
            .id(UUID.randomUUID())
            .ownerId(UUID.randomUUID()) // Different owner
            .name("Kos 3")
            .address("Address 3")
            .description("Description 3")
            .price(1800000.0)
            .maxCapacity(10)
            .build();
    
            List<Kos> allKosList = List.of(kos1, kos2, kos3);
            when(kosService.getAllKos()).thenReturn(CompletableFuture.completedFuture(allKosList));
    
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
            List<Booking> ownerBookings = bookingService.findBookingsByOwnerId(ownerId).get();
    
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
        public void testCancelBooking() throws ExecutionException, InterruptedException { // Add throws
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

            // Fix: Handle async call properly
            CompletableFuture<Optional<Booking>> result = bookingService.findBookingById(booking.getBookingId());
            Optional<Booking> retrievedBooking = result.get(); // Now properly handled with throws
            assertTrue(retrievedBooking.isPresent());
            assertEquals(BookingStatus.CANCELLED, retrievedBooking.get().getStatus());
        }

        @Test
        public void testFindBookingByIdAsync() throws ExecutionException, InterruptedException {
            // Setup
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

            // Execute
            CompletableFuture<Optional<Booking>> result = bookingService.findBookingById(booking.getBookingId());
            Optional<Booking> foundBooking = result.get();

            // Verify
            assertTrue(foundBooking.isPresent());
            assertEquals(booking.getBookingId(), foundBooking.get().getBookingId());
        }

        @Test
        public void testFindBookingByIdNotFoundAsync() throws ExecutionException, InterruptedException {
            UUID randomId = UUID.randomUUID();
            when(bookingRepository.findById(randomId)).thenReturn(Optional.empty());

            CompletableFuture<Optional<Booking>> result = bookingService.findBookingById(randomId);
            Optional<Booking> foundBooking = result.get();

            assertFalse(foundBooking.isPresent());
        }

        @Test
        public void testFindAllBookingsAsync() throws ExecutionException, InterruptedException {
            // Setup - Create booking for this test
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
            List<Booking> expectedBookings = List.of(booking);
            when(bookingRepository.findAll()).thenReturn(expectedBookings);

            // Execute
            CompletableFuture<List<Booking>> result = bookingService.findAllBookings();
            List<Booking> actualBookings = result.get();

            // Verify
            assertEquals(expectedBookings.size(), actualBookings.size());
            assertEquals(expectedBookings.get(0).getBookingId(), actualBookings.get(0).getBookingId());
        }
    
        @Test
        public void testClearStore() {
            // Test the clearStore method
            bookingService.clearStore();
    
            // Verify that deleteAll was called on the repository
            verify(bookingRepository).deleteAll();
        }

        @Test
        public void testFindBookingsByUserIdAsync() throws ExecutionException, InterruptedException {
            // Setup
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
            when(bookingRepository.findAll()).thenReturn(List.of(booking));

            // Execute
            CompletableFuture<List<Booking>> result = bookingService.findBookingsByUserId(userId);
            List<Booking> userBookings = result.get();

            // Verify
            assertEquals(1, userBookings.size());
            assertEquals(userId, userBookings.get(0).getUserId());
        }
    

        @Test
        void updateBooking_shouldValidateAdvanceBooking_whenDateChanged() {
            // Setup: Create existing booking
            Booking existingBooking = new Booking(
                    UUID.randomUUID(),
                    userId,
                    kosId,
                    LocalDate.now().plusDays(5),
                    3,
                    monthlyPrice,
                    fullName,
                    phoneNumber,
                    BookingStatus.PENDING_PAYMENT
            );
    
            // Create updated booking with new check-in date
            Booking updatedBooking = new Booking(
                    existingBooking.getBookingId(),
                    userId,
                    kosId,
                    LocalDate.now().plusDays(10), // Changed date
                    3,
                    monthlyPrice,
                    "Updated Name",
                    phoneNumber,
                    BookingStatus.PENDING_PAYMENT
            );
    
            when(bookingRepository.findById(existingBooking.getBookingId()))
                    .thenReturn(Optional.of(existingBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
    
            // Execute
            bookingService.updateBooking(updatedBooking);
    
            // Verify
            verify(stateValidator).validateForUpdate(existingBooking);
            verify(stateValidator).validateBookingAdvance(LocalDate.now().plusDays(10));
            verify(stateValidator).validateBasicFields(updatedBooking);
        }
    
        @Test
        void updateBooking_shouldNotValidateAdvanceBooking_whenDateUnchanged() {
            // Setup: Create existing booking
            LocalDate checkInDate = LocalDate.now().plusDays(5);
    
            Booking existingBooking = new Booking(
                    UUID.randomUUID(),
                    userId,
                    kosId,
                    checkInDate,
                    3,
                    monthlyPrice,
                    fullName,
                    phoneNumber,
                    BookingStatus.PENDING_PAYMENT
            );
    
            // Create updated booking with same check-in date
            Booking updatedBooking = new Booking(
                    existingBooking.getBookingId(),
                    userId,
                    kosId,
                    checkInDate, // Same date
                    3,
                    monthlyPrice,
                    "Updated Name", // Only name changed
                    phoneNumber,
                    BookingStatus.PENDING_PAYMENT
            );
    
            when(bookingRepository.findById(existingBooking.getBookingId()))
                    .thenReturn(Optional.of(existingBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
    
            // Execute
            bookingService.updateBooking(updatedBooking);
    
            // Verify
            verify(stateValidator).validateForUpdate(existingBooking);
            verify(stateValidator, never()).validateBookingAdvance(any(LocalDate.class));
            verify(stateValidator).validateBasicFields(updatedBooking);
        }
    
        @Test
        void cancelBooking_shouldReturnRoomToAvailable() {
            // Setup
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
    
            when(bookingRepository.findById(booking.getBookingId()))
                    .thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    
            // Execute
            bookingService.cancelBooking(booking.getBookingId());
    
            // Verify
            verify(stateValidator).validateForCancellation(booking);
            verify(kosService).addAvailableRoom(booking.getKosId());
    
            ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
            verify(bookingRepository).save(bookingCaptor.capture());
            assertEquals(BookingStatus.CANCELLED, bookingCaptor.getValue().getStatus());
        }
    
    }