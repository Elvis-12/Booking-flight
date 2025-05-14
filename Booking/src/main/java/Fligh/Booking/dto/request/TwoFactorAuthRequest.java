package Fligh.Booking.dto.request;

import lombok.Data;

@Data
public class TwoFactorAuthRequest {
    
    private String code;
}