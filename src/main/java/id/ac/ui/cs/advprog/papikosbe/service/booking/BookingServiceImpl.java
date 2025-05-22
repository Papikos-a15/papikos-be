package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.PaymentService;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final KosService kosService;
    private final PaymentService paymentService;
    private final BookingValidator stateValidator;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              KosService kosService,
                              PaymentService paymentService,
                              BookingValidator stateValidator,
                              BookingAccessValidator bookingAccessValidator) {
        this.bookingRepository = bookingRepository;
        this.kosService = kosService;
        this.paymentService = paymentService;
        this.stateValidator = stateValidator;}

    @Override
    public Booking createBooking(Booking booking) {
        // Validate booking data
        stateValidator.validateBasicFields(booking);

        // Set booking ID if not provided
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }

        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));
        booking.setMonthlyPrice(kos.getPrice());

        // Ensure initial status is PENDING_PAYMENT
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> findBookingById(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public void updateBooking(Booking booking) {
        // Validate booking data
        stateValidator.validateBasicFields(booking);

        Booking existingBooking = bookingRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForUpdate(existingBooking);

        // Preserve the current status
        BookingStatus currentStatus = existingBooking.getStatus();
        booking.setStatus(currentStatus);

        // Update the booking
        bookingRepository.save(booking);
    }

    @Override
    public void payBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForPayment(booking);

        // Get kos to get owner ID
        Kos kos = kosService.getKosById(booking.getKosId())
                .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

        // Create payment
        paymentService.createPayment(
                booking.getUserId(),
                kos.getOwnerId(),
                BigDecimal.valueOf(booking.getTotalPrice())
        );

        // Update booking status to PAID
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
    }

    @Override
    public void approveBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForApproval(booking);

        // Update booking status to APPROVED
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Validate state transition
        stateValidator.validateForCancellation(booking);

        // Update booking status to CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> findBookingsByOwnerId(UUID ownerId) {
        // Get all kos owned by this owner
        List<Kos> ownerKos = kosService.getAllKos().stream()
                .filter(kos -> kos.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());

        // Extract kos IDs
        List<UUID> ownerKosIds = ownerKos.stream()
                .map(Kos::getId)
                .collect(Collectors.toList());

        // Find all bookings for these kos
        return bookingRepository.findAll().stream()
                .filter(booking -> ownerKosIds.contains(booking.getKosId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findBookingsByUserId(UUID userId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }


    @Override
    public void clearStore() {
        bookingRepository.deleteAll();
    }
}