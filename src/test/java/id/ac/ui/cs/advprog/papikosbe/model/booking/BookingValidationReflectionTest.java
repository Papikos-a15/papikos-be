package id.ac.ui.cs.advprog.papikosbe.model.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidationReflectionTest {

    private Booking validBooking;

    @BeforeEach
    void init() {
        validBooking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(2),  // lolos H+1
                2,
                750_000,
                "John Doe",
                "08123456789",
                BookingStatus.PENDING_PAYMENT
        );
    }

    /* ---------- helper memanggil method private ---------- */
    private static InvocationTargetException invokePrivate(Object target, String methodName)
            throws NoSuchMethodException, IllegalAccessException {
        Method m = target.getClass().getDeclaredMethod(methodName);
        m.setAccessible(true);
        try {
            m.invoke(target);
            return null;                     // sukses
        } catch (InvocationTargetException ex) {
            return ex;                       // gagal – bungkus exception
        }
    }

    /* =========================================================
     * validateUpdate() – semua cabang
     * ========================================================= */
    @Test
    void validateUpdate_durationNegative() throws Exception {
        validBooking.setDuration(-3);
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Duration must be at least 1 month", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_monthlyPriceNegative() throws Exception {
        validBooking.setMonthlyPrice(-1);
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Monthly price must be greater than 0", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_fullNameNull() throws Exception {
        validBooking.setFullName(null);
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Full name cannot be empty", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_phoneNumberNull() throws Exception {
        validBooking.setPhoneNumber(null);
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Phone number cannot be empty", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_allValid_passes() throws Exception {
        assertNull(invokePrivate(validBooking, "validateUpdate"));
    }

    /* =========================================================
     * validateNewBooking() – semua cabang
     * ========================================================= */
    @Test
    void validateNewBooking_checkInToday_throws() throws Exception {
        validBooking.setCheckInDate(LocalDate.now());          // < tomorrow
        InvocationTargetException ex = invokePrivate(validBooking, "validateNewBooking");
        assertEquals("Booking must be made at least 1 day in advance to allow owner approval time",
                ex.getCause().getMessage());
    }

    @Test
    void validateNewBooking_validData_passes() throws Exception {
        // H+2 & semua field valid (dari @BeforeEach)
        assertNull(invokePrivate(validBooking, "validateNewBooking"));
    }

    @Test
    void validateNewBooking_validDateButBadFields_propagatesFromValidateUpdate() throws Exception {
        // check-in sudah lolos H+1
        validBooking.setFullName("");                          // akan gagal di validateUpdate()
        InvocationTargetException ex = invokePrivate(validBooking, "validateNewBooking");
        assertEquals("Full name cannot be empty", ex.getCause().getMessage());
    }

    /* =========================================================
     *  validateUpdate() – cabang sisa (duration==0, price==0,
     *                     whitespace string)
     * ========================================================= */

    @Test
    void validateUpdate_durationZero_shouldThrow() throws Exception {
        validBooking.setDuration(0);                            // = 0
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Duration must be at least 1 month", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_monthlyPriceZero_shouldThrow() throws Exception {
        validBooking.setMonthlyPrice(0);                        // = 0
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Monthly price must be greater than 0", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_fullNameWhitespace_shouldThrow() throws Exception {
        validBooking.setFullName("   ");                        // whitespace
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Full name cannot be empty", ex.getCause().getMessage());
    }

    @Test
    void validateUpdate_phoneWhitespace_shouldThrow() throws Exception {
        validBooking.setPhoneNumber("   ");                     // whitespace
        InvocationTargetException ex = invokePrivate(validBooking, "validateUpdate");
        assertEquals("Phone number cannot be empty", ex.getCause().getMessage());
    }

    /* =========================================================
     *  validateNewBooking() – propagasi error lain (price==0)
     * ========================================================= */
    @Test
    void validateNewBooking_validDateButPriceZero_shouldPropagate() throws Exception {
        validBooking.setMonthlyPrice(0);                        // valid H+1, tapi price salah
        InvocationTargetException ex = invokePrivate(validBooking, "validateNewBooking");
        assertEquals("Monthly price must be greater than 0", ex.getCause().getMessage());
    }

}