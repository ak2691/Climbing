package com.allan.climberanalyzer.UserHandling.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    private String email;
    private String verificationCode;
}
