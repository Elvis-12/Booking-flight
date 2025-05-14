package Fligh.Booking.service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import Fligh.Booking.model.User;
import Fligh.Booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class TwoFactorAuthService {

    @Value("${app.twoFactor.issuer}")
    private String issuer;

    @Autowired
    private UserRepository userRepository;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    public String generateNewSecret() {
        return secretGenerator.generate();
    }

    public String getQrCodeImageUri(String secret, String username) {
        // Use direct QrData.Builder instead of QrDataFactory
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1) // default
                .digits(6) // default
                .period(30) // default
                .build();

        byte[] imageData;
        try {
            imageData = qrGenerator.generate(data);
        } catch (QrGenerationException e) {
            throw new RuntimeException("Error generating QR code", e);
        }

        return getDataUriForImage(imageData, qrGenerator.getImageMimeType());
    }

    public boolean validateCode(String code, String secret) {
        return codeVerifier.isValidCode(secret, code);
    }

    @Transactional
    public void enableTwoFactorAuth(User user, String secret) {
        user.setMfaSecret(secret);
        user.setMfaEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void disableTwoFactorAuth(User user) {
        user.setMfaSecret(null);
        user.setMfaEnabled(false);
        userRepository.save(user);
    }
}