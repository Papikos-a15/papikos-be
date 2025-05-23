package id.ac.ui.cs.advprog.papikosbe.controller.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.config.SecurityConfig;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingService;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.papikosbe.util.AuthenticationUtils;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@WithMockUser
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private KosService kosService;

    @MockBean
    private BookingValidator stateValidator;

    @MockBean
    private BookingAccessValidator bookingAccessValidator;

    @MockBean
    private AuthenticationUtils authUtils;

    @MockBean
    private JwtTokenProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking sample;
    private Kos sampleKos;
    private UUID userId;
    private UUID ownerId;
    private UUID kosId;
    private double monthlyPrice;
    private String fullName;
    private String phoneNumber;

    @BeforeEach
    void setup() {
        // Initialize data for new fields
        monthlyPrice = 1500000.0;
        fullName = "John Doe";
        phoneNumber = "081234567890";
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        kosId = UUID.randomUUID();

        // Create sample booking with complete data
        sample = new Booking(
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

        // Create sample kos
        sampleKos = new Kos();
        sampleKos.setId(kosId);
        sampleKos.setOwnerId(ownerId);
        sampleKos.setName("Test Kos");
        sampleKos.setPrice(monthlyPrice);

        // JWT validation stubs
        when(jwtProvider.validate("tok")).thenReturn(true);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jwtProvider.getAuthentication("tok")).thenReturn(auth);

        // Authentication utils stubs
        when(authUtils.getUserIdFromAuth(any())).thenReturn(userId);
    }

    @Test
    void createBooking_returnsOk() throws Exception {
        when(bookingService.createBooking(any()))
                .thenReturn(sample);

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.fullName").value(fullName))
                .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.monthlyPrice").value(monthlyPrice));
    }

    @Test
    void getAllBookings_returnsUserBookingsOnly() throws Exception {
        // Create booking for current user
        Booking userBooking = new Booking(
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

        // Create booking for different user (shouldn't be returned)
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

        // Mock authentication to return the user ID
        when(authUtils.getUserIdFromAuth(any())).thenReturn(userId);

        // Mock service to return only user's bookings
        when(bookingService.findBookingsByUserId(userId))
                .thenReturn(List.of(userBooking));

        mockMvc.perform(get("/api/bookings")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(userBooking.getBookingId().toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingById_found() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(sample));
        when(kosService.getKosById(sample.getKosId())).thenReturn(Optional.of(sampleKos));

        // Allow access for this user
        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);

        mockMvc.perform(get("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(sample.getBookingId().toString()));
    }

    @Test
    void getBookingById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(bookingService.findBookingById(randomId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bookings/{id}", randomId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingById_forbidden() throws Exception {
        UUID differentUserId = UUID.randomUUID();
        Booking otherUserBooking = new Booking(
                UUID.randomUUID(),
                differentUserId, // Different user
                kosId,
                LocalDate.now().plusDays(1),
                2,
                monthlyPrice,
                fullName,
                phoneNumber,
                BookingStatus.PENDING_PAYMENT
        );

        when(bookingService.findBookingById(otherUserBooking.getBookingId()))
                .thenReturn(Optional.of(otherUserBooking));
        when(kosService.getKosById(otherUserBooking.getKosId())).thenReturn(Optional.of(sampleKos));

        // Throw exception for access validation
        doThrow(new IllegalStateException("Only the tenant who made the booking can perform this action"))
                .when(bookingAccessValidator).validateUserAccess(userId, differentUserId);
        doThrow(new IllegalStateException("Only the kos owner can perform this action"))
                .when(bookingAccessValidator).validateOwnerAccess(ownerId, userId);

        mockMvc.perform(get("/api/bookings/{id}", otherUserBooking.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelBooking_returnsNoContent() throws Exception {
        UUID id = sample.getBookingId();
        doNothing().when(bookingService).cancelBooking(id);

        mockMvc.perform(delete("/api/bookings/{id}", id)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBooking_returnsOk() throws Exception {
        // Prepare an updated booking
        Booking updatedBooking = new Booking(
                sample.getBookingId(),
                sample.getUserId(),
                sample.getKosId(),
                LocalDate.now().plusDays(7), // new date
                3, // new duration
                monthlyPrice,
                "Jane Doe", // new name
                "089876543210", // new phone
                BookingStatus.PENDING_PAYMENT
        );

        // Mock the find call to get existing booking
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(sample));

        // Allow access for this user
        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);

        // Mock findById after update
        when(bookingService.findBookingById(updatedBooking.getBookingId()))
                .thenReturn(Optional.of(updatedBooking));

        // Mock the service to accept the update
        doNothing().when(bookingService).updateBooking(any(Booking.class));

        mockMvc.perform(put("/api/bookings/{id}", updatedBooking.getBookingId())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("089876543210"));
    }

    @Test
    void updateBooking_idMismatch_returnsBadRequest() throws Exception {
        // ID in path doesn't match ID in body
        UUID differentId = UUID.randomUUID();

        mockMvc.perform(put("/api/bookings/{id}", differentId)
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_afterApproval_returnsForbidden() throws Exception {
        // Create approved booking
        Booking approvedBooking = new Booking(
                sample.getBookingId(),
                sample.getUserId(),
                sample.getKosId(),
                sample.getCheckInDate(),
                sample.getDuration(),
                sample.getMonthlyPrice(),
                sample.getFullName(),
                sample.getPhoneNumber(),
                BookingStatus.APPROVED
        );

        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(approvedBooking));

        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);

        // Throw exception when trying to update an approved booking
        doThrow(new IllegalStateException("Cannot edit booking after it has been approved or cancelled"))
                .when(bookingService).updateBooking(any(Booking.class));

        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBooking_notFound_returnsNotFound() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isNotFound());
    }

    @Test
    void payBooking_returnsOk() throws Exception {
        // Setup: Create a booking with updated status
        Booking paidBooking = new Booking(
                sample.getBookingId(),
                sample.getUserId(),
                sample.getKosId(),
                sample.getCheckInDate(),
                sample.getDuration(),
                sample.getMonthlyPrice(),
                sample.getFullName(),
                sample.getPhoneNumber(),
                BookingStatus.PAID // paid status
        );

        // Mock service method calls
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(sample), Optional.of(paidBooking));

        // Allow access for this user
        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);
        doNothing().when(bookingService).payBooking(sample.getBookingId());

        mockMvc.perform(post("/api/bookings/{id}/pay", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void payBooking_notFound_returnsNotFound() throws Exception {
        // Non-existent booking ID
        UUID nonExistentId = UUID.randomUUID();
        when(bookingService.findBookingById(nonExistentId))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/bookings/{id}/pay", nonExistentId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveBooking_returnsOk() throws Exception {
        // Setup: Create paid booking
        Booking paidBooking = new Booking(
                sample.getBookingId(),
                sample.getUserId(),
                sample.getKosId(),
                sample.getCheckInDate(),
                sample.getDuration(),
                sample.getMonthlyPrice(),
                sample.getFullName(),
                sample.getPhoneNumber(),
                BookingStatus.PAID
        );

        // Setup: Create approved booking (after approval)
        Booking approvedBooking = new Booking(
                sample.getBookingId(),
                sample.getUserId(),
                sample.getKosId(),
                sample.getCheckInDate(),
                sample.getDuration(),
                sample.getMonthlyPrice(),
                sample.getFullName(),
                sample.getPhoneNumber(),
                BookingStatus.APPROVED
        );

        // Mock kos retrieval
        when(kosService.getKosById(kosId)).thenReturn(Optional.of(sampleKos));

        // Mock booking retrieval (before and after approval)
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(Optional.of(paidBooking), Optional.of(approvedBooking));

        // Override auth to return owner ID
        when(authUtils.getUserIdFromAuth(any())).thenReturn(ownerId);

        // Allow access for owner
        doNothing().when(bookingAccessValidator).validateOwnerAccess(ownerId, ownerId);
        doNothing().when(bookingService).approveBooking(sample.getBookingId());

        mockMvc.perform(post("/api/bookings/{id}/approve", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingsByOwnerId_returnsList() throws Exception {
        List<Booking> ownerBookings = Arrays.asList(sample);
        when(bookingService.findBookingsByOwnerId(ownerId))
                .thenReturn(ownerBookings);

        mockMvc.perform(get("/api/bookings/owner/{ownerId}", ownerId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(sample.getBookingId().toString()));
    }

}