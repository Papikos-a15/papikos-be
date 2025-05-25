package id.ac.ui.cs.advprog.papikosbe.repository.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.PaymentBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentBookingRepository extends JpaRepository<PaymentBooking, UUID> {

    Optional<PaymentBooking> findByPaymentId(UUID paymentId);

    Optional<PaymentBooking> findByBookingId(UUID bookingId);

    @Query("SELECT pb FROM PaymentBooking pb WHERE pb.paymentId = :paymentId")
    Optional<PaymentBooking> findPaymentBookingByPaymentId(@Param("paymentId") UUID paymentId);

    @Query("SELECT pb FROM PaymentBooking pb WHERE pb.bookingId = :bookingId")
    Optional<PaymentBooking> findPaymentBookingByBookingId(@Param("bookingId") UUID bookingId);

    boolean existsByPaymentId(UUID paymentId);

    boolean existsByBookingId(UUID bookingId);
}