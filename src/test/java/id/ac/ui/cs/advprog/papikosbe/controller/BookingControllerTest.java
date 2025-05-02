package id.ac.ui.cs.advprog.papikosbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking sample;

    @BeforeEach
    void setup() {
        sample = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                2,
                BookingStatus.PENDING_PAYMENT
        );
    }

    @Test
    void createBooking_returnsCreated() throws Exception {
        Mockito.when(bookingService.createBooking(Mockito.any()))
                .thenReturn(sample);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    void getAllBookings_returnsList() throws Exception {
        Mockito.when(bookingService.findAllBookings())
                .thenReturn(Arrays.asList(sample));

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(sample.getBookingId().toString()));
    }

    @Test
    void getBookingById_found() throws Exception {
        Mockito.when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(sample));

        mockMvc.perform(get("/bookings/{id}", sample.getBookingId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()));
    }

    @Test
    void getBookingById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        Mockito.when(bookingService.findBookingById(randomId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/bookings/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_returnsNoContent() throws Exception {
        UUID id = sample.getBookingId();
        Mockito.doNothing().when(bookingService).cancelBooking(id);

        mockMvc.perform(delete("/bookings/{id}", id))
                .andExpect(status().isNoContent());
    }
}
