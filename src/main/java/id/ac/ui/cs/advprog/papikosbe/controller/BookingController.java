package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/add")
    public Booking addBooking() {
        Booking booking = new Booking(
                UUID.randomUUID(),          // bookingId
                UUID.randomUUID(),          // userId
                UUID.randomUUID(),          // kosId
                LocalDate.now().plusDays(1),// checkInDate: besok
                3,                          // duration: 3 bulan
                BookingStatus.PENDING_PAYMENT  // status awal
        );
        return bookingService.createBooking(booking);
    }
}
