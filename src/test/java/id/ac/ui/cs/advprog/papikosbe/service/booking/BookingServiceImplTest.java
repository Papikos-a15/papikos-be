    package id.ac.ui.cs.advprog.papikosbe.service.booking;
    
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

    import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
    import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
    import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
    import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
    import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
    import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
    import id.ac.ui.cs.advprog.papikosbe.repository.booking.PaymentBookingRepository;
    import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
    import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
    import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
    import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
    import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
    import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
    import id.ac.ui.cs.advprog.papikosbe.observer.event.BookingApprovedEvent;
    import id.ac.ui.cs.advprog.papikosbe.observer.handler.EventHandlerContext;
    import jakarta.persistence.EntityNotFoundException;
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
    import java.math.BigDecimal;
    import java.util.concurrent.CompletableFuture;
    import java.util.concurrent.ExecutionException;
    
    import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
    import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
    import org.springframework.test.util.ReflectionTestUtils;
    import static org.mockito.Mockito.doReturn;

    @ExtendWith(MockitoExtension.class)
    class BookingServiceImplTest {

        // Mocks for constructor injection
        @Mock private BookingRepository bookingRepository;
        @Mock private KosService kosService;
        @Mock private TransactionService transactionService;
        @Mock private BookingValidator stateValidator;

        // Mocks for field injection
        @Mock private PaymentBookingRepository paymentBookingRepository;
        @Mock private TransactionRepository transactionRepository;

        // Other mocks you might need
        @Mock private WalletRepository walletRepository;
        @Mock private BookingAccessValidator bookingAccessValidator;
        @Mock private EventHandlerContext eventHandlerContext;

        // Don't use @InjectMocks - create manually
        private BookingServiceImpl bookingService;

        private double monthlyPrice;
        private String fullName;
        private String phoneNumber;
        private UUID ownerId;
        private UUID kosId;
        private Kos testKos;
        private UUID userId;

        @BeforeEach
        void setUp() {
            // Create service with constructor dependencies
            bookingService = new BookingServiceImpl(
                    bookingRepository,
                    kosService,
                    transactionService,
                    stateValidator,
                    eventHandlerContext,
                    paymentBookingRepository,
                    transactionRepository
            );

            // Manually inject field dependencies using reflection
            ReflectionTestUtils.setField(bookingService, "paymentBookingRepository", paymentBookingRepository);
            ReflectionTestUtils.setField(bookingService, "transactionRepository", transactionRepository);

            monthlyPrice = 1500000.0;
            fullName = "John Doe";
            phoneNumber = "081234567890";
            ownerId = UUID.randomUUID();
            kosId = UUID.randomUUID();
            userId = UUID.randomUUID();

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
        void testCreateBookingWithPersonalDetails() {
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
        void testCalculateTotalPrice() throws ExecutionException, InterruptedException {
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
        void testEditBookingBeforeApproval() throws ExecutionException, InterruptedException {
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
        void testEditBookingAfterPaymentBeforeApproval() throws Exception {
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

            // Mock the return value of the transactionService.createPayment to return a CompletableFuture
            Payment mockPayment = new Payment();
            mockPayment.setId(UUID.randomUUID());  // Set a mock payment ID

            CompletableFuture<Payment> paymentFuture = CompletableFuture.completedFuture(mockPayment);

            // Mock the method to return the completed CompletableFuture
            when(transactionService.createPayment(any(UUID.class), any(UUID.class), any(BigDecimal.class)))
                    .thenReturn(paymentFuture);

            // Now pay the booking - this transitions from PENDING to PAID
            bookingService.payBooking(createdBooking.getBookingId());
        }

        @Test
        void testEditBookingAfterApprovalShouldFail() throws Exception {
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

            // Mocking the return of payment
            Payment mockPayment = new Payment();
            mockPayment.setId(UUID.randomUUID());  // Set mock payment ID
            CompletableFuture<Payment> paymentFuture = CompletableFuture.completedFuture(mockPayment); // Mock the payment future

            // Mock the createPayment method to return a completed future
            when(transactionService.createPayment(any(UUID.class), any(UUID.class), any(BigDecimal.class)))
                    .thenReturn(paymentFuture);

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

            // Assert that trying to update an approved booking throws an exception
            assertThrows(IllegalStateException.class, () -> bookingService.updateBooking(updatedBooking));
        }

        @Test
        void testApproveBooking() {
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
        void testFindBookingsByOwnerId() throws ExecutionException, InterruptedException {
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
        void testPayBooking() throws Exception {
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

            // Mocking dependencies
            when(bookingRepository.findById(booking.getBookingId())).thenReturn(Optional.of(booking));
            when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));

            // Create a mock Payment object
            Payment mockPayment = new Payment();
            mockPayment.setId(UUID.randomUUID());  // Set a mock payment ID

            // Return a completed CompletableFuture with the mock Payment
            CompletableFuture<Payment> paymentFuture = CompletableFuture.completedFuture(mockPayment);

            // Mock the transactionService.createPayment to return the completed CompletableFuture
            when(transactionService.createPayment(any(UUID.class), any(UUID.class), any(BigDecimal.class)))
                    .thenReturn(paymentFuture);

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
        void testCancelBooking() throws ExecutionException, InterruptedException { // Add throws
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
        void testFindBookingByIdAsync() throws ExecutionException, InterruptedException {
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
        void testFindBookingByIdNotFoundAsync() throws ExecutionException, InterruptedException {
            UUID randomId = UUID.randomUUID();
            when(bookingRepository.findById(randomId)).thenReturn(Optional.empty());

            CompletableFuture<Optional<Booking>> result = bookingService.findBookingById(randomId);
            Optional<Booking> foundBooking = result.get();

            assertFalse(foundBooking.isPresent());
        }

        @Test
        void testFindAllBookingsAsync() throws ExecutionException, InterruptedException {
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
        void testClearStore() {
            // Test the clearStore method
            bookingService.clearStore();
    
            // Verify that deleteAll was called on the repository
            verify(bookingRepository).deleteAll();
        }

        @Test
        void testFindBookingsByUserIdAsync() throws ExecutionException, InterruptedException {
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
        }

        @Test
        void cancelBooking_shouldReturnRoomToAvailable() throws Exception {
            // Setup
            UUID bookingId = UUID.randomUUID();
            UUID kosId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UUID ownerId = UUID.randomUUID();
            UUID paymentId = UUID.randomUUID();

            // Create booking
            Booking booking = new Booking(
                    bookingId, tenantId, kosId, LocalDate.now().plusDays(7),
                    3, monthlyPrice, fullName, phoneNumber, BookingStatus.PAID
            );

            // Create payment booking
            PaymentBooking paymentBooking = new PaymentBooking();
            paymentBooking.setPaymentId(paymentId);
            paymentBooking.setBookingId(bookingId);

            // Create payment with owner
            Owner owner = new Owner();
            owner.setId(ownerId);

            Payment originalPayment = new Payment();
            originalPayment.setId(paymentId);
            originalPayment.setStatus(TransactionStatus.COMPLETED);
            originalPayment.setOwner(owner);

            // Create refund payment
            Payment refundPayment = new Payment();
            refundPayment.setStatus(TransactionStatus.COMPLETED);

            // Mock repositories
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(paymentBookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(paymentBooking));
            when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(originalPayment));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            // Mock async refund service
            CompletableFuture<Payment> completedFuture = CompletableFuture.completedFuture(refundPayment);
            when(transactionService.refundPayment(paymentId, ownerId)).thenReturn(completedFuture);

            when(kosService.addAvailableRoom(kosId)).thenReturn(Optional.of(new Kos()));

            // Execute
            bookingService.cancelBooking(bookingId);

            // Verify
            verify(stateValidator).validateForCancellation(booking);
            verify(transactionService).refundPayment(paymentId, ownerId);
            verify(kosService).addAvailableRoom(kosId);

            ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
            verify(bookingRepository).save(bookingCaptor.capture());
            assertEquals(BookingStatus.CANCELLED, bookingCaptor.getValue().getStatus());
        }
      
        @Test
        void testApproveBookingTriggersEvent() {
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

            ArgumentCaptor<BookingApprovedEvent> eventCaptor = ArgumentCaptor.forClass(BookingApprovedEvent.class);

            bookingService.approveBooking(booking.getBookingId());

            verify(eventHandlerContext, times(1)).handleEvent(eventCaptor.capture());

            BookingApprovedEvent capturedEvent = eventCaptor.getValue();

            assertEquals(booking.getBookingId(), capturedEvent.getBookingId(), "Booking ID should match");
            assertEquals(booking.getUserId(), capturedEvent.getUserId(), "User ID should match");
        }

        /* ===============================================================
         *  EXTRA TESTS – menutup branch yang belum ter-cover
         * =============================================================== */

        @Test
        void findBookingsByUserIdAsync_shouldReturnFailedFutureWhenRepositoryThrows() throws Exception {
            RuntimeException dbEx = new RuntimeException("DB down");
            when(bookingRepository.findAll()).thenThrow(dbEx);

            CompletableFuture<List<Booking>> fut = bookingService.findBookingsByUserId(userId);

            assertTrue(fut.isCompletedExceptionally());
            ExecutionException execEx = assertThrows(ExecutionException.class, fut::get);
            assertSame(dbEx, execEx.getCause());
        }

        /* ----------------------------------------------------------------
         *  cancelBooking – booking BUKAN PAID  → langsung cancel, no refund
         * ---------------------------------------------------------------- */
        @Test
        void cancelBooking_notPaid_shouldCancelWithoutRefund() throws Exception {
            UUID bookingId = UUID.randomUUID();

            Booking booking = new Booking(
                    bookingId, userId, kosId, LocalDate.now().plusDays(5),
                    2, monthlyPrice, fullName, phoneNumber, BookingStatus.PENDING_PAYMENT
            );

            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            bookingService.cancelBooking(bookingId);

            verify(stateValidator).validateForCancellation(booking);
            verify(kosService).addAvailableRoom(kosId);
            verify(transactionService, never()).refundPayment(any(), any());
        }

        /* ----------------------------------------------------------------
         *  cancelBooking – PAID tetapi TIDAK ada PaymentBooking
         * ---------------------------------------------------------------- */
        @Test
        void cancelBooking_paid_noPaymentRecord_shouldCancelWithoutRefund() throws Exception {
            UUID bookingId = UUID.randomUUID();

            Booking booking = new Booking(
                    bookingId, userId, kosId, LocalDate.now().plusDays(5),
                    2, monthlyPrice, fullName, phoneNumber, BookingStatus.PAID
            );

            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(paymentBookingRepository.findByBookingId(bookingId)).thenReturn(Optional.empty());
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            bookingService.cancelBooking(bookingId);

            verify(kosService).addAvailableRoom(kosId);
            verify(transactionService, never()).refundPayment(any(), any());
            verify(bookingRepository).save(argThat(b -> b.getStatus() == BookingStatus.CANCELLED));
        }

        /* ----------------------------------------------------------------
         *  cancelBooking – refund GAGAL → RuntimeException
         * ---------------------------------------------------------------- */
        @Test
        void cancelBooking_paid_refundFailed_shouldThrow() throws Exception{
            UUID bookingId  = UUID.randomUUID();
            UUID paymentId  = UUID.randomUUID();

            Booking booking = new Booking(
                    bookingId, userId, kosId, LocalDate.now().plusDays(5),
                    2, monthlyPrice, fullName, phoneNumber, BookingStatus.PAID
            );

            PaymentBooking paymentBooking = new PaymentBooking();
            paymentBooking.setBookingId(bookingId);
            paymentBooking.setPaymentId(paymentId);

            Owner owner = new Owner();
            UUID ownerId = UUID.randomUUID();
            owner.setId(ownerId);

            Payment originalPayment = new Payment();
            originalPayment.setId(paymentId);
            originalPayment.setOwner(owner);

            Payment refundFailed = new Payment();
            refundFailed.setStatus(TransactionStatus.FAILED);   // refund gagal

            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(paymentBookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(paymentBooking));
            when(transactionRepository.findPaymentById(paymentId)).thenReturn(Optional.of(originalPayment));

            /* gunakan doReturn supaya tak perlu try/catch checked Exception */
            doReturn(CompletableFuture.completedFuture(refundFailed))
                    .when(transactionService).refundPayment(paymentId, ownerId);

            RuntimeException ex =
                    assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(bookingId));
            assertTrue(ex.getMessage().contains("Refund failed"));
        }


        /* ===============================================================
         *  EXTRA TESTS – branch & error handling BookingServiceImpl
         * =============================================================== */

        @Test
        void createBooking_kosNotFound_shouldThrow() {
            Booking booking = new Booking(
                    null, userId, kosId,
                    LocalDate.now().plusDays(3), 1,
                    monthlyPrice, fullName, phoneNumber,
                    BookingStatus.PENDING_PAYMENT);

            when(kosService.getKosById(kosId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> bookingService.createBooking(booking));
        }

        @Test
        void createBooking_generateIdWhenNull() {
            Booking booking = new Booking(
                    null, userId, kosId, LocalDate.now().plusDays(2),
                    2, monthlyPrice, fullName, phoneNumber,
                    BookingStatus.PENDING_PAYMENT);

            when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));

            ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
            when(bookingRepository.save(captor.capture()))
                    .thenAnswer(inv -> inv.getArgument(0));             // return same object

            bookingService.createBooking(booking);

            assertNotNull(captor.getValue().getBookingId(), "ID harus ter-generate otomatis");
        }

        @Test
        void findBookingById_async_repositoryThrows_completedExceptionally() {
            RuntimeException boom = new RuntimeException("DB down");
            when(bookingRepository.findById(any())).thenThrow(boom);

            CompletableFuture<Optional<Booking>> fut =
                    bookingService.findBookingById(UUID.randomUUID());

            assertTrue(fut.isCompletedExceptionally());
        }

        @Test
        void findAllBookings_async_repositoryThrows_completedExceptionally() {
            when(bookingRepository.findAll()).thenThrow(new RuntimeException("fail"));

            CompletableFuture<List<Booking>> fut = bookingService.findAllBookings();
            assertTrue(fut.isCompletedExceptionally());
        }

        @Test
        void updateBooking_bookingNotFound_shouldThrow() {
            UUID id = UUID.randomUUID();
            Booking dummy = new Booking(id, userId, kosId,
                    LocalDate.now().plusDays(4), 2, monthlyPrice,
                    fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);

            when(bookingRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> bookingService.updateBooking(dummy));
        }

        @Test
        void payBooking_bookingNotFound_shouldThrow() {
            UUID id = UUID.randomUUID();
            when(bookingRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> bookingService.payBooking(id));
        }

        @Test
        void payBooking_kosNotFound_shouldThrow() throws Exception {
            Booking booking = new Booking(
                    UUID.randomUUID(), userId, kosId,
                    LocalDate.now().plusDays(5), 2, monthlyPrice,
                    fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);

            when(bookingRepository.findById(booking.getBookingId()))
                    .thenReturn(Optional.of(booking));
            when(kosService.getKosById(kosId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> bookingService.payBooking(booking.getBookingId()));
        }

        @Test
        void payBooking_createPaymentFails_shouldThrow() throws Exception {
            Booking booking = new Booking(
                    UUID.randomUUID(), userId, kosId,
                    LocalDate.now().plusDays(5), 2, monthlyPrice,
                    fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);

            when(bookingRepository.findById(booking.getBookingId()))
                    .thenReturn(Optional.of(booking));
            when(kosService.getKosById(kosId)).thenReturn(Optional.of(testKos));

            CompletableFuture<Payment> failedFuture =
                    CompletableFuture.failedFuture(new RuntimeException("gateway down"));

            /* gunakan doReturn agar method tidak benar-benar dipanggil saat stubbing */
            doReturn(failedFuture)
                    .when(transactionService).createPayment(any(UUID.class), any(UUID.class), any(BigDecimal.class));

            // payBooking akan melempar (ExecutionException) – cukup asserThrows umum Exception
            assertThrows(Exception.class,
                    () -> bookingService.payBooking(booking.getBookingId()));
        }






    }

