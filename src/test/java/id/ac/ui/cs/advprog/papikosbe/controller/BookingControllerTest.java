package id.ac.ui.cs.advprog.papikosbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.config.SecurityConfig;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.Booking;
import id.ac.ui.cs.advprog.papikosbe.service.BookingService;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)           // muat konfigurasi Security termasuk JwtFilter
@AutoConfigureMockMvc                   // attach seluruh SecurityFilterChain
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtTokenProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking sample;

    @BeforeEach
    void setup() {
        // contoh booking
        sample = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                2,
                BookingStatus.PENDING_PAYMENT
        );

        // stub JWT validasi & authentication untuk token "tok"
        when(jwtProvider.validate("tok")).thenReturn(true);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jwtProvider.getAuthentication("tok")).thenReturn(auth);
    }

    @Test
    void createBooking_returnsCreated() throws Exception {
        when(bookingService.createBooking(any()))
                .thenReturn(sample);

        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    void getAllBookings_returnsList() throws Exception {
        when(bookingService.findAllBookings())
                .thenReturn(Arrays.asList(sample));

        mockMvc.perform(get("/bookings")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(sample.getBookingId().toString()));
    }

    @Test
    void getBookingById_found() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(sample));

        mockMvc.perform(get("/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()));
    }

    @Test
    void getBookingById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(bookingService.findBookingById(randomId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/bookings/{id}", randomId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_returnsNoContent() throws Exception {
        UUID id = sample.getBookingId();
        doNothing().when(bookingService).cancelBooking(id);

        mockMvc.perform(delete("/bookings/{id}", id)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNoContent());
    }
}
