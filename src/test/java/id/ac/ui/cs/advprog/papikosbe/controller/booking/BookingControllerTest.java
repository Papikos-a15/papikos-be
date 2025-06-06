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
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.doThrow;

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

        when(authUtils.getUserIdFromAuth(any())).thenReturn(userId);

        // Fix: Mock service to return CompletableFuture
        when(bookingService.findBookingsByUserId(userId))
                .thenReturn(CompletableFuture.completedFuture(List.of(userBooking)));

        mockMvc.perform(get("/api/bookings")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk()) // Fix: andExpected → andExpect
                .andExpect(jsonPath("$[0].bookingId").value(userBooking.getBookingId().toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingById_found() throws Exception {
        // Fix: Mock service to return CompletableFuture
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)));
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
        // Fix: Mock service to return CompletableFuture with empty Optional
        when(bookingService.findBookingById(randomId))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

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

        // Fix: Mock service to return CompletableFuture
        when(bookingService.findBookingById(otherUserBooking.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(otherUserBooking)));
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

        // Fix: Mock the find call to return CompletableFuture (first call for existing booking)
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(updatedBooking))); // second call after update

        // Allow access for this user
        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);

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

        // Fix: Mock service to return CompletableFuture
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(approvedBooking)));

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
        // Fix: Mock service to return CompletableFuture with empty Optional
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

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

        // Fix: Mock service method calls to return CompletableFuture
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)))   // first call
                .thenReturn(CompletableFuture.completedFuture(Optional.of(paidBooking))); // second call after payment

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
        // Fix: Mock service to return CompletableFuture with empty Optional
        when(bookingService.findBookingById(nonExistentId))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

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

        // Fix: Mock booking retrieval to return CompletableFuture (before and after approval)
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(paidBooking)))     // first call
                .thenReturn(CompletableFuture.completedFuture(Optional.of(approvedBooking))); // second call after approval

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
        List<Booking> ownerBookings = Collections.singletonList(sample);
        // Mock the service to return a completed CompletableFuture, as the service method is still async
        // but the controller now calls .join() on it.
        when(bookingService.findBookingsByOwnerId(ownerId))
                .thenReturn(CompletableFuture.completedFuture(ownerBookings));

        mockMvc.perform(get("/api/bookings/owner/{ownerId}", ownerId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(sample.getBookingId().toString()));
    }

    /* ===========================================================
     *   EXTRA BRANCH-TESTS  –  BookingController
     * =========================================================== */

    @Test
    void createBooking_userMismatch_returnsForbidden() throws Exception {
        // authenticated user ≠ userId di body
        UUID otherUser = UUID.randomUUID();
        Booking wrongUserBooking = new Booking(
                UUID.randomUUID(), otherUser, kosId,
                LocalDate.now().plusDays(2), 2, monthlyPrice,
                fullName, phoneNumber, BookingStatus.PENDING_PAYMENT);

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongUserBooking)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBooking_serviceThrowsEntityNotFound_returnsNotFound() throws Exception {
        when(bookingService.createBooking(any()))
                .thenThrow(new EntityNotFoundException("Kos not found"));

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookings_serviceException_returns500() throws Exception {
        when(bookingService.findBookingsByUserId(userId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("boom")));

        mockMvc.perform(get("/api/bookings")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateBooking_serviceThrowsIllegalArgument_returnsBadRequest() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)));

        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);
        doThrow(new IllegalArgumentException("bad data"))
                .when(bookingService).updateBooking(any());

        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void payBooking_forbidden_returns403() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)));

        doThrow(new IllegalStateException("no access"))
                .when(bookingAccessValidator).validateUserAccess(userId, sample.getUserId());

        mockMvc.perform(post("/api/bookings/{id}/pay", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isForbidden());
    }

    @Test
    void approveBooking_forbidden_returns403() throws Exception {
        Booking paid = new Booking(sample.getBookingId(), userId, kosId,
                sample.getCheckInDate(), sample.getDuration(), monthlyPrice,
                fullName, phoneNumber, BookingStatus.PAID);

        when(bookingService.findBookingById(paid.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(paid)));

        when(kosService.getKosById(kosId)).thenReturn(Optional.of(sampleKos));

        doThrow(new IllegalStateException("not owner"))
                .when(bookingAccessValidator).validateOwnerAccess(ownerId, userId);

        // auth user = tenant (bukan owner)
        when(authUtils.getUserIdFromAuth(any())).thenReturn(userId);

        mockMvc.perform(post("/api/bookings/{id}/approve", paid.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isForbidden());
    }

    @Test
    void approveBooking_notFound_returns404() throws Exception {
        UUID random = UUID.randomUUID();
        when(bookingService.findBookingById(random))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        mockMvc.perform(post("/api/bookings/{id}/approve", random)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_serviceThrowsIllegalState_returns403() throws Exception {
        UUID id = sample.getBookingId();
        doThrow(new IllegalStateException("cant cancel"))
                .when(bookingService).cancelBooking(id);

        mockMvc.perform(delete("/api/bookings/{id}", id)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelBooking_notFound_returns404() throws Exception {
        UUID id = sample.getBookingId();
        doThrow(new EntityNotFoundException("not found"))
                .when(bookingService).cancelBooking(id);

        mockMvc.perform(delete("/api/bookings/{id}", id)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsByOwnerId_serviceException_returns500() throws Exception {
        when(bookingService.findBookingsByOwnerId(ownerId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("boom")));

        mockMvc.perform(get("/api/bookings/owner/{id}", ownerId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isInternalServerError());
    }

    /* =========================================================
     *  EXTRA COVERAGE – 500 / 400 / 403 paths yang tersisa
     * ========================================================= */

    /* ---------- createBooking: IllegalArgumentException → 400 ---------- */
    @Test
    void createBooking_validationError_returnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("bad data"))
                .when(bookingService).createBooking(any());

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isBadRequest());
    }

    /* ---------- createBooking: IllegalStateException → 403 ---------- */
    @Test
    void createBooking_illegalState_returnsForbidden() throws Exception {
        doThrow(new IllegalStateException("state"))
                .when(bookingService).createBooking(any());

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isForbidden());
    }

    /* ---------- getBookingById: generic Exception → 500 ---------- */
    @Test
    void getBookingById_genericError_returns500() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("boom")));

        mockMvc.perform(get("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isInternalServerError());
    }

    /* ---------- updateBooking: generic Exception → 500 ---------- */
    @Test
    void updateBooking_genericError_returns500() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)));

        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);
        doThrow(new RuntimeException("unexpected"))
                .when(bookingService).updateBooking(any());

        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isInternalServerError());
    }

    /* ---------- payBooking: generic Exception → 500 ---------- */
    @Test
    void payBooking_genericError_returns500() throws Exception {
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(sample)));

        doNothing().when(bookingAccessValidator).validateUserAccess(userId, userId);
        doThrow(new RuntimeException("unexpected"))
                .when(bookingService).payBooking(sample.getBookingId());

        mockMvc.perform(post("/api/bookings/{id}/pay", sample.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isInternalServerError());
    }

    /* ---------- approveBooking: generic Exception → 500 ---------- */
    @Test
    void approveBooking_genericError_returns500() throws Exception {
        Booking paid = new Booking(
                sample.getBookingId(), userId, kosId,
                sample.getCheckInDate(), sample.getDuration(),
                monthlyPrice, fullName, phoneNumber,
                BookingStatus.PAID);

        when(bookingService.findBookingById(paid.getBookingId()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(paid)));
        when(kosService.getKosById(kosId)).thenReturn(Optional.of(sampleKos));

        when(authUtils.getUserIdFromAuth(any())).thenReturn(ownerId);
        doNothing().when(bookingAccessValidator).validateOwnerAccess(ownerId, ownerId);

        doThrow(new RuntimeException("unexpected"))
                .when(bookingService).approveBooking(paid.getBookingId());

        mockMvc.perform(post("/api/bookings/{id}/approve", paid.getBookingId())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isInternalServerError());
    }

    /* ---------- findAllBookings: service exception sudah diuji;
               tambahan untuk updateBooking .join() melempar CompletionException ---------- */
    @Test
    void updateBooking_joinThrowsCompletionException_returns500() throws Exception {
        // first call for existing booking fails
        when(bookingService.findBookingById(sample.getBookingId()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

        mockMvc.perform(put("/api/bookings/{id}", sample.getBookingId())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isInternalServerError());
    }



}