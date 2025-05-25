package id.ac.ui.cs.advprog.papikosbe.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Data
public class TopUpRequest {
    private BigDecimal amount;

    public TopUpRequest() {}

    public TopUpRequest(BigDecimal amount) {
        this.amount = amount;
    }

}