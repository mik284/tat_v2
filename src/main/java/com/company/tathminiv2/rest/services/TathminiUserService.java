package com.company.tathminiv2.rest.services;

import com.company.tathminiv2.entity.InvestorStatusEnum;
import com.company.tathminiv2.entity.OTP;
import com.company.tathminiv2.entity.RiskProfileEnum;
import com.company.tathminiv2.entity.TathminiUser;
import com.company.tathminiv2.repository.TathminiUserRepository;
import com.company.tathminiv2.rest.dto.LoginDto;
import com.company.tathminiv2.rest.dto.OtpDto;
import com.company.tathminiv2.rest.dto.PasswordResetDto;
import com.company.tathminiv2.rest.exeptions.ItemAlreadyExistsExeption;
import com.company.tathminiv2.rest.exeptions.ResourceNotFoundExeption;
import com.company.tathminiv2.rest.dto.RegisterDto;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import io.jmix.data.PersistenceHints;
import io.jmix.email.EmailException;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.reflections.Reflections.log;

@Service
public class TathminiUserService {

    private final TathminiUserRepository tathminiUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DataManager dataManager;
    private final Emailer emailer;

    public TathminiUserService(TathminiUserRepository tathminiUserRepository, BCryptPasswordEncoder passwordEncoder, DataManager dataManager, Emailer emailer) {
        this.tathminiUserRepository = tathminiUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.dataManager = dataManager;
        this.emailer = emailer;
    }

    // Send OTP via email
    public void sendOtpToUser(String email, int otpCode, String emailBodyTemplate) throws EmailException {
        String emailBody = String.format(emailBodyTemplate, otpCode);
        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setAddresses(email)
                .setSubject("OTP for Authentication")
                .setFrom("tathminisupport@gmail.com")
                .setBody(emailBody)
                .build();
        emailer.sendEmail(emailInfo);
    }

    // Register a new user
    public String registerService(RegisterDto registerDto) {
        log.info("Registering user: {}", registerDto);
        try {
            Optional<TathminiUser> existingUserByUsername = dataManager.load(TathminiUser.class)
                    .query("select t from TathminiUser t where t.username = :username")
                    .parameter("username", registerDto.getUsername())
                    .optional();

            if (existingUserByUsername.isPresent()) {
                throw new ItemAlreadyExistsExeption("User with this username already exists: " + registerDto.getUsername());
            }

            Optional<TathminiUser> existingUserByEmail = dataManager.load(TathminiUser.class)
                    .query("select t from TathminiUser t where t.email = :email")
                    .parameter("email", registerDto.getEmail())
                    .optional();

            if (existingUserByEmail.isPresent()) {
                throw new ItemAlreadyExistsExeption("User with this email already exists: " + registerDto.getEmail());
            }

            TathminiUser user = dataManager.create(TathminiUser.class);
            user.setUsername(registerDto.getUsername());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setEmail(registerDto.getEmail());
            user.setDob(registerDto.getDob());
            user.setPhoneNumber(registerDto.getPhone());
            user.setRiskProfile(RiskProfileEnum.MODERATE);
            user.setInvestorStatus(InvestorStatusEnum.PENDING);

            tathminiUserRepository.save(user);
            return "User successfully registered";

        } catch (Exception e) {
            log.error("Error during registration", e);
            return e.getMessage();
        }
    }

    // Login method with OTP generation
    public String login(LoginDto loginDto) {
        TathminiUser existingUser = dataManager.load(TathminiUser.class)
                .query("select m from TathminiUser m where m.username = :username")
                .parameter("username", loginDto.getUsername())
                .optional()
                .orElseThrow(() -> new ResourceNotFoundExeption("User not found"));

        log.info("Existing user: {}", existingUser);

        if (passwordEncoder.matches(loginDto.getPassword(), existingUser.getPassword())) {
            int otpCode = otpGenerator();
            OTP otp = dataManager.create(OTP.class);
            otp.setOtpUsed(false);
            otp.setOtpCode(otpCode);
            otp.setPurpose("login");
            otp.setTathminiUser(existingUser);
            otp.setExpiritationTime(OffsetDateTime.now().plus(15, ChronoUnit.MINUTES));// 15 minutes

            try {
                sendOtpToUser(existingUser.getEmail(), otpCode, "Your OTP for login authentication is: %d");
                dataManager.save(otp);
                log.info("OTP created with expiration time: {}", otp.getCreatedBy());
                return "OTP successfully sent. Please check your email.";
            } catch (EmailException e) {
                throw new RuntimeException("Failed to send OTP. Please try again later.");
            }
        }
        return "Please enter correct username and password";
    }

    // Verify OTP
    public ResponseEntity<?> verifyOtp(OtpDto otpDto) {
        TathminiUser user = dataManager.load(TathminiUser.class)
                .query("select m from TathminiUser m where m.username = :username")
                .parameter("username", otpDto.getUserName())
                .optional()
                .orElseThrow(() -> new ResourceNotFoundExeption("User not found"));

        log.info("Existing user: {}", user);

        OTP otp = dataManager.load(OTP.class)
                .query("select o from OTP o where o.tathminiUser = :tathminiUser and o.otpUsed = false")
                .parameter("tathminiUser", user)
                .maxResults(1)
                .optional()
                .orElseThrow(() -> new ResourceNotFoundExeption("No valid OTP found"));

        log.info("Verifying OTP: {}, User OTP: {}, Current time: {}, Expiration time: {}",
                otpDto.getOtp(), otp.getOtpCode(), new Date(), otp.getOtpCode());

        if (otp.getOtpCode().equals(otpDto.getOtp())) {
            if (!isOtpExpired(otp)) {
                otp.setOtpUsed(true);
                dataManager.save(
                        new SaveContext()
                                .removing(otp)
                                .setHint(PersistenceHints.SOFT_DELETION, false)
                );
                return ResponseEntity.ok(user);
            } else {
                log.warn("OTP expired. Current time: {}, Expiration time: {}", new Date(), otp.getExpiritationTime());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired OTP");
            }
        } else {
            log.warn("Invalid OTP. Expected: {}, Received: {}", otp.getOtpCode(), otpDto.getOtp());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }

    // Initiate password reset
    public ResponseEntity<String> initiatePasswordReset(String email) throws EmailException {
        log.info("Initiating password reset for email: {}", email);

        try {
            Optional<TathminiUser> userOptional = dataManager.load(TathminiUser.class)
                    .query("select m from TathminiUser m where m.email = :email")
                    .parameter("email", email)
                    .optional();

            if (userOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
            }

            TathminiUser user = userOptional.get();
            int otpCode = otpGenerator();
            OTP otp = dataManager.create(OTP.class);
            otp.setOtpUsed(false);
            otp.setOtpCode(otpCode);
            otp.setPurpose("password_reset");
            otp.setTathminiUser(user);
            otp.setExpiritationTime(OffsetDateTime.now().plus(15, ChronoUnit.MINUTES)); // 30 minutes

            sendOtpToUser(user.getEmail(), otpCode, "Your OTP for password reset is: %d");
            dataManager.save(otp);

            return new ResponseEntity<>("OTP sent, please check your email", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during password reset initiation for email: {}", email, e);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<String> updatePassword(PasswordResetDto passwordResetDto) {


        try {
            TathminiUser user = dataManager.load(TathminiUser.class)
                    .query("select m from TathminiUser m where m.email = :email")
                    .parameter("email", passwordResetDto.getEmail())
                    .optional()
                    .orElseThrow(() -> new ResourceNotFoundExeption("Email not found"));

            if(!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())){
                return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
            }

            user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
            tathminiUserRepository.save(user);
            return new ResponseEntity<>("Password updated", HttpStatus.OK);
        } catch (ResourceNotFoundExeption e) {
            throw new RuntimeException(e);
        }
}
    // Check if OTP is expired
    private boolean isOtpExpired(OTP otp) {
        log.debug("Checking if OTP is expired.");
        try {
            OffsetDateTime expirationTime = otp.getExpiritationTime();
            if (expirationTime == null) {
                log.warn("Expiration time is null. Considering OTP expired.");
                return true;
            }

            boolean isExpired = expirationTime.isBefore(OffsetDateTime.now());
            log.debug("Is OTP expired? {}", isExpired);
            return isExpired;
        } catch (Exception e) {
            log.error("Error checking OTP expiration", e);
            return true; // Consider expired in case of error
        }
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}