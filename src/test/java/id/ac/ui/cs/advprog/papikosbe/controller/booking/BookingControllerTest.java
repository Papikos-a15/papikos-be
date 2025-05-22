//package id.ac.ui.cs.advprog.papikosbe.controller.booking;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import id.ac.ui.cs.advprog.papikosbe.config.SecurityConfig;
//import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
//import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
//import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingService;
//import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(BookingController.class)
//@Import(SecurityConfig.class)           // muat konfigurasi Security termasuk JwtFilter
//@AutoConfigureMockMvc                   // attach seluruh SecurityFilterChain
//class BookingControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private BookingService bookingService;
//
//    @MockBean
//    private JwtTokenProvider jwtProvider;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private Booking sample;
//    private double monthlyPrice;
//    private String fullName;
//    private String phoneNumber;
//
//    @BeforeEach
//    void setup() {
//        // Initialize data for new fields
//        monthlyPrice = 1500000.0;
//        fullName = "John Doe";
//        phoneNumber = "081234567890";
//
//        // contoh booking with complete data
//        sample = new Booking(
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                LocalDate.now().plusDays(1),
//                2,
//                monthlyPrice,
//                fullName,
//                phoneNumber,
//                BookingStatus.PENDING_PAYMENT
//        );
//
//        // stub JWT validasi & authentication untuk token "tok"
//        when(jwtProvider.validate("tok")).thenReturn(true);
//        Authentication auth = new UsernamePasswordAuthenticationToken(
//                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//        when(jwtProvider.getAuthentication("tok")).thenReturn(auth);
//    }
//
//    @Test
//    void createBooking_returnsCreated() throws Exception {
//        when(bookingService.createBooking(any()))
//                .thenReturn(sample);
//
//        mockMvc.perform(post("/api/bookings")
//                        .header("Authorization", "Bearer tok")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sample)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()))
//                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
//                .andExpect(jsonPath("$.fullName").value(fullName))
//                .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
//                .andExpect(jsonPath("$.monthlyPrice").value(monthlyPrice));
//    }
//
//    @Test
//    void getAllBookings_returnsList() throws Exception {
//        when(bookingService.findAllBookings())
//                .thenReturn(Arrays.asList(sample));
//
//        mockMvc.perform(get("/api/bookings")
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].bookingId").value(sample.getBookingId().toString()));
//    }
//
//    @Test
//    void getBookingById_found() throws Exception {
//        when(bookingService.findBookingById(sample.getBookingId()))
//                .thenReturn(Optional.of(sample));
//
//        mockMvc.perform(get("/api/bookings/{id}", sample.getBookingId())
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()));
//    }
//
//    @Test
//    void getBookingById_notFound() throws Exception {
//        UUID randomId = UUID.randomUUID();
//        when(bookingService.findBookingById(randomId))
//                .thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/bookings/{id}", randomId)
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void cancelBooking_returnsNoContent() throws Exception {
//        UUID id = sample.getBookingId();
//        doNothing().when(bookingService).cancelBooking(id);
//
//        mockMvc.perform(delete("/api/bookings/{id}", id)
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isNoContent());
//    }
//
//    // New tests for update functionality
//
//    @Test
//    void updateBooking_returnsOk() throws Exception {
//        // Prepare an updated booking
//        Booking updatedBooking = new Booking(
//                sample.getBookingId(),
//                sample.getUserId(),
//                sample.getKosId(),
//                LocalDate.now().plusDays(7), // new date
//                3, // new duration
//                monthlyPrice,
//                "Jane Doe", // new name
//                "089876543210", // new phone
//                BookingStatus.PENDING_PAYMENT
//        );
//
//        // Mock the service to accept the update
//        doNothing().when(bookingService).updateBooking(any(Booking.class));
//
//        mockMvc.perform(put("/api/bookings/{id}", updatedBooking.getBookingId())
//                        .header("Authorization", "Bearer tok")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedBooking)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
//                .andExpect(jsonPath("$.phoneNumber").value("089876543210"));
//    }
//
//    @Test
//    void updateBooking_idMismatch_returnsBadRequest() throws Exception {
//        // ID in path doesn't match ID in body
//        UUID differentId = UUID.randomUUID();
//
//        mockMvc.perform(put("/api/bookings/{id}", differentId)
//                        .header("Authorization", "Bearer tok")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sample)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateBooking_afterApproval_returnsForbidden() throws Exception {
//        // Mock service to throw exception when trying to update an approved booking
//        doThrow(new IllegalStateException("Cannot edit booking after it has been paid or cancelled"))
//                .when(bookingService).updateBooking(any(Booking.class));
//
//        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
//                        .header("Authorization", "Bearer tok")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sample)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void updateBooking_notFound_returnsNotFound() throws Exception {
//        // Mock service to throw EntityNotFoundException
//        doThrow(new EntityNotFoundException("Booking not found"))
//                .when(bookingService).updateBooking(any(Booking.class));
//
//        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
//                        .header("Authorization", "Bearer tok")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sample)))
//                .andExpect(status().isNotFound());
//    }
//    @Test
//    void payBooking_returnsOk() throws Exception {
//        // Setup: Create a booking with updated status
//        Booking paidBooking = new Booking(
//                sample.getBookingId(),
//                sample.getUserId(),
//                sample.getKosId(),
//                sample.getCheckInDate(),
//                sample.getDuration(),
//                sample.getMonthlyPrice(),
//                sample.getFullName(),
//                sample.getPhoneNumber(),
//                BookingStatus.PAID // paid status
//        );
//
//        // Mock the service methods
//        doNothing().when(bookingService).payBooking(sample.getBookingId());
//        when(bookingService.findBookingById(sample.getBookingId())).thenReturn(Optional.of(paidBooking));
//
//        mockMvc.perform(post("/api/bookings/{id}/pay", sample.getBookingId())
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("PAID"));
//
//        verify(bookingService).payBooking(sample.getBookingId());
//    }
//
//    @Test
//    void payBooking_notFound_returnsNotFound() throws Exception {
//        // Mock service to throw EntityNotFoundException
//        UUID nonExistentId = UUID.randomUUID();
//        doThrow(new EntityNotFoundException("Booking not found"))
//                .when(bookingService).payBooking(nonExistentId);
//
//        mockMvc.perform(post("/api/bookings/{id}/pay", nonExistentId)
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void payBooking_invalidTransition_returnsForbidden() throws Exception {
//        // Mock service to throw IllegalStateException for invalid transition
//        doThrow(new IllegalStateException("Cannot pay for a booking that is not in PENDING_PAYMENT status"))
//                .when(bookingService).payBooking(sample.getBookingId());
//
//        mockMvc.perform(post("/api/bookings/{id}/pay", sample.getBookingId())
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void approveBooking_returnsOk() throws Exception {
//        // Setup: Create a booking with updated status
//        Booking approvedBooking = new Booking(
//                sample.getBookingId(),
//                sample.getUserId(),
//                sample.getKosId(),
//                sample.getCheckInDate(),
//                sample.getDuration(),
//                sample.getMonthlyPrice(),
//                sample.getFullName(),
//                sample.getPhoneNumber(),
//                BookingStatus.APPROVED // approved status
//        );
//
//        // Mock the service methods
//        doNothing().when(bookingService).approveBooking(sample.getBookingId());
//        when(bookingService.findBookingById(sample.getBookingId())).thenReturn(Optional.of(approvedBooking));
//
//        mockMvc.perform(post("/api/bookings/{id}/approve", sample.getBookingId())
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("APPROVED"));
//
//        verify(bookingService).approveBooking(sample.getBookingId());
//    }
//
//    @Test
//    void approveBooking_notFound_returnsNotFound() throws Exception {
//        // Mock service to throw EntityNotFoundException
//        UUID nonExistentId = UUID.randomUUID();
//        doThrow(new EntityNotFoundException("Booking not found"))
//                .when(bookingService).approveBooking(nonExistentId);
//
//        mockMvc.perform(post("/api/bookings/{id}/approve", nonExistentId)
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void approveBooking_invalidTransition_returnsForbidden() throws Exception {
//        // Mock service to throw IllegalStateException for invalid transition
//        doThrow(new IllegalStateException("Cannot approve a booking that is not in PAID status"))
//                .when(bookingService).approveBooking(sample.getBookingId());
//
//        mockMvc.perform(post("/api/bookings/{id}/approve", sample.getBookingId())
//                        .header("Authorization", "Bearer tok"))
//                .andExpect(status().isForbidden());
//    }
//}