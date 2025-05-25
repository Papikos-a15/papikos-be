package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.ValidationRequirement;
import id.ac.ui.cs.advprog.papikosbe.exception.ValidationException;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers the default logic in ValidationRule and all branches in BaseValidationRule.
 */
class ValidationFrameworkTest {

    /* ------------------------------------------------------------------
     * Dummy rule   →  memberi kita kendali penuh untuk mem-icu setiap cabang
     * ------------------------------------------------------------------ */
    private static class DummyRule extends BaseValidationRule {

        private final String operationType;
        private final ValidationRequirement requirement;
        private final boolean throwIllegalState;

        DummyRule(String op, ValidationRequirement req, boolean fail) {
            this.operationType = op;
            this.requirement   = req;
            this.throwIllegalState = fail;
        }

        @Override
        public boolean supports(String operation, BookingStatus status) {
            // selalu true agar tak mempengaruhi pengujian
            return true;
        }

        @Override
        public String getOperationType() {
            return operationType;
        }

        @Override
        public ValidationRequirement getRequirements() {
            return requirement;
        }

        @Override
        protected void doValidate(ValidationContext context) {
            if (throwIllegalState) {
                throw new IllegalStateException("boom!");
            }
            // else: sukses
        }
    }

    /* ================================================================
     * 1. contextMeetsRequirements ­— semua kombinasi enum
     * ================================================================ */
    @Nested
    class ContextRequirementTests {

        final Booking dummyBooking = new Booking();   // cukup non-null
        final Kos     dummyKos     = new Kos();       // cukup non-null
        final UUID    requesterId  = UUID.randomUUID();

        @Test
        void bookingOnly() {
            ValidationRule rule = new DummyRule("TEST", ValidationRequirement.BOOKING_ONLY, false);

            // ✅ booking ada  → true
            ValidationContext ok = ValidationContext.builder().booking(dummyBooking).build();
            assertTrue(rule.contextMeetsRequirements(ok));

            // ❌ booking null → false
            ValidationContext bad = ValidationContext.builder().build();
            assertFalse(rule.contextMeetsRequirements(bad));
        }

        @Test
        void bookingAndKos() {
            ValidationRule rule = new DummyRule("TEST", ValidationRequirement.BOOKING_AND_KOS, false);

            // ✅ keduanya ada
            ValidationContext ok = ValidationContext.builder()
                    .booking(dummyBooking).kos(dummyKos).build();
            assertTrue(rule.contextMeetsRequirements(ok));

            // ❌ kos hilang
            ValidationContext bad = ValidationContext.builder()
                    .booking(dummyBooking).build();
            assertFalse(rule.contextMeetsRequirements(bad));
        }

        @Test
        void bookingAndRequester() {
            ValidationRule rule = new DummyRule("TEST", ValidationRequirement.BOOKING_AND_REQUESTER, false);

            // ✅ keduanya ada
            ValidationContext ok = ValidationContext.builder()
                    .booking(dummyBooking).requesterId(requesterId).build();
            assertTrue(rule.contextMeetsRequirements(ok));

            // ❌ requester hilang
            ValidationContext bad = ValidationContext.builder()
                    .booking(dummyBooking).build();
            assertFalse(rule.contextMeetsRequirements(bad));
        }

        @Test
        void fullContext() {
            ValidationRule rule = new DummyRule("TEST", ValidationRequirement.FULL_CONTEXT, false);

            // ✅ semua ada
            ValidationContext ok = ValidationContext.builder()
                    .booking(dummyBooking).kos(dummyKos).requesterId(requesterId).build();
            assertTrue(rule.contextMeetsRequirements(ok));

            // ❌ salah satu hilang
            ValidationContext bad = ValidationContext.builder()
                    .booking(dummyBooking).kos(dummyKos).build();
            assertFalse(rule.contextMeetsRequirements(bad));
        }

        @Test
        void nullContextAlwaysFalse() {
            ValidationRule rule = new DummyRule("TEST", ValidationRequirement.BOOKING_ONLY, false);
            assertFalse(rule.contextMeetsRequirements(null));
        }
    }

    /* ================================================================
     * 2. BaseValidationRule.validate – empat cabang utama
     * ================================================================ */
    @Nested
    class BaseRuleValidateTests {

        final Booking booking = new Booking();  // hanya butuh non-null

        @Test
        void nullContextThrowsIllegalArgument() {
            DummyRule rule = new DummyRule("OP", ValidationRequirement.BOOKING_ONLY, false);
            assertThrows(IllegalArgumentException.class, () -> rule.validate(null));
        }

        @Test
        void contextNotMeetingRequirementsThrowsIllegalArgument() {
            DummyRule rule = new DummyRule("OP", ValidationRequirement.BOOKING_ONLY, false);

            // context tanpa booking
            ValidationContext ctx = ValidationContext.builder().operation("OPERATION").build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> rule.validate(ctx));
            assertTrue(ex.getMessage().contains("Context doesn't meet requirements"));
        }

        @Test
        void successfulValidationPassesSilently() {
            DummyRule rule = new DummyRule("OP", ValidationRequirement.BOOKING_ONLY, false);

            ValidationContext ctx = ValidationContext.builder()
                    .booking(booking).operation("OPERATION").build();

            assertDoesNotThrow(() -> rule.validate(ctx));
        }

        @Test
        void illegalStateInsideDoValidateConvertedToValidationException() {
            DummyRule rule = new DummyRule("OP", ValidationRequirement.BOOKING_ONLY, true);

            ValidationContext ctx = ValidationContext.builder()
                    .booking(booking).operation("OPERATION").build();

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> rule.validate(ctx));

            // Pastikan pesan & metadata tepat
            assertEquals("boom!", ex.getMessage());
            assertEquals("OP", ex.getValidationRule());     // OperationType
            assertEquals("OPERATION", ex.getOperation());   // ctx.getOperation()
        }
    }

}
