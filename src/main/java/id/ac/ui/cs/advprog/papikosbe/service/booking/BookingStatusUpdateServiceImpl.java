package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementation of BookingStatusUpdateService that handles the automatic
 * update of bookings that have expired (passed their end date)
 */
@Service
@Slf4j
public class BookingStatusUpdateServiceImpl implements BookingStatusUpdateService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingStatusUpdateServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    public void scheduledExpiredBookingsUpdate() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async("bookingTaskExecutor")
    public CompletableFuture<Integer> updateExpiredBookingsAsync() {
        return null;
    }
}