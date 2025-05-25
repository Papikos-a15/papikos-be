package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ValidationContext {
    private Booking booking;
    private Kos kos;
    private UUID requesterId;
    private String operation;

    public boolean hasKos() {
        return kos != null;
    }

    public boolean hasRequester() {
        return requesterId != null;
    }

}