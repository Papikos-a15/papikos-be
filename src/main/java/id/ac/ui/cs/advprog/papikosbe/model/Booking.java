import java.time.LocalDate;
import java.util.UUID;

public class Booking {
    private final UUID bookingId;
    private final UUID userId;
    private final UUID kosId; // Hanya referensi ke model Kos
    private final LocalDate checkInDate;
    private final int duration;
    private BookingStatus status;
    
    // Constructor skeleton
    public Booking(UUID bookingId, UUID userId, UUID kosId, LocalDate checkInDate, int duration, BookingStatus status) {
        // Belum lengkap: Validasi hanya sebagai placeholder, sehingga test validasi masih gagal
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        this.bookingId = bookingId;
        this.userId = userId;
        this.kosId = kosId;
        this.checkInDate = checkInDate;
        this.duration = duration;
        this.status = status;
    }
    
    // Getter untuk semua field
    public UUID getBookingId() {
        return bookingId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public UUID getKosId() {
        return kosId;
    }
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    // Skeleton method untuk menghitung total harga sewa
    public double calculateTotalPrice(double monthlyPrice) {
        // Pada tahap ini, kita belum implementasi perhitungan dengan lengkap
        // Agar test calculateTotalPrice gagal, bisa dipaksa mengembalikan nilai yang salah
        // Contoh:
        // return 0.0;  // Sementara, sehingga test akan gagal 
        // Untuk memenuhi uji coba, kita bisa mengembalikan kalkulasi sederhana:
        return monthlyPrice * duration;
    }
}
