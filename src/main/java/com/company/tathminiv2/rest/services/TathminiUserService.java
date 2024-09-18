package com.company.tathminiv2.rest.services;

import com.company.tathminiv2.entity.InvestorStatusEnum;
import com.company.tathminiv2.entity.OTP;
import com.company.tathminiv2.entity.RiskProfileEnum;
import com.company.tathminiv2.entity.TathminiUser;
import com.company.tathminiv2.repository.TathminiUserRepository;
import com.company.tathminiv2.rest.dto.LoginDto;
import com.company.tathminiv2.rest.dto.OtpDto;
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
import io.jmix.rest.annotation.RestMethod;
import io.jmix.rest.annotation.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.reflections.Reflections.log;

//@RestService("/api")
@Service
public class TathminiUserService {

    private final TathminiUserRepository tathminiUserRepository;
    @Autowired
    private DataManager dataManager;



    @Autowired
    private Emailer emailer;

    public TathminiUserService(TathminiUserRepository tathminiUserRepository) {
        this.tathminiUserRepository = tathminiUserRepository;
    }


    //    @RestMethod
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

//    @RestMethod
    public String registerService(RegisterDto registerDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Registering user: " + registerDto);
            try {
                 Optional<TathminiUser> existingUser = dataManager.load(TathminiUser.class)
                        .query("select t from TathminiUser t " +
                                "where t.username = :username")
                        .parameter("username", registerDto.getUsername())
                        .optional();

                log.info("Existing user: " + existingUser);

                if (existingUser.isPresent()) {
                    throw new ItemAlreadyExistsExeption("User already exists: " + registerDto.getUsername());
                } else {

                    TathminiUser user = dataManager.create(TathminiUser.class);
                    user.setUsername(registerDto.getUsername());
                    user.setPassword(registerDto.getPassword());
                    user.setFirstName(registerDto.getFirstName());
                    user.setLastName(registerDto.getLastName());
                    user.setEmail(registerDto.getEmail());
                    user.setDob(registerDto.getDob());
                    user.setPhoneNumber(registerDto.getPhone());
                    user.setRiskProfile(RiskProfileEnum.MODERATE);
                    user.setInvestorStatus(InvestorStatusEnum.PENDING);

                    tathminiUserRepository.save(user);
//                    dataManager.save(user);
                    return "User successfully registered";
                }

            }
            catch (Exception e) {
                log.error("Error during registration", e);
                return e.getMessage();
            }

    }

    @RestMethod
    public String login(LoginDto loginDto) {
        TathminiUser existingUser = dataManager.load(TathminiUser.class)
                .query("select m from TathminiUser m where m.username = :username")
                .parameter("username", loginDto.getUsername())
                .optional()
                .orElseThrow(() -> new ResourceNotFoundExeption("User not found"));
        log.info("Existing user: " + existingUser);

        if (existingUser.getPassword().equals(loginDto.getPassword())) {

            int otpCode = otpGenerator();
            OTP otp = dataManager.create(OTP.class);
            otp.setOtpUsed(false);
            otp.setOtpCode(otpCode);
            otp.setPurpose("login");
            otp.setTathminiUser(existingUser);
            otp.setExpiritationTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));

            try {
                String emailBodyTemplate = "Your OTP for login authentication is: %d";
                sendOtpToUser(existingUser.getEmail(), otpCode, emailBodyTemplate);
                dataManager.save(otp);
                return "OTP successfully sent. Please check your email.";
            } catch (EmailException e) {
                throw new RuntimeException("Failed to send OTP. Please try again later.");
            }

        }

        return "Please enter correct username and password";
    }

    @RestMethod
    public ResponseEntity<?> verifyOtp(OtpDto otpDto) {
        TathminiUser user = dataManager.load(TathminiUser.class)
                .query("select m from TathminiUser m where m.username = :username")
                .parameter("username", otpDto.getUserName())
                .optional()
                .orElseThrow(() -> new ResourceNotFoundExeption("User not found"));

        log.info("Existing user: " + user);


        OTP otp = dataManager.load(OTP.class)
                .query("select o from OTP o where o.tathminiUser = :tathminiUser and o.otpUsed = false")
                .parameter("tathminiUser", user)
                .maxResults(1)
                .optional()
                .orElseThrow(() -> new ResourceNotFoundExeption("No valid OTP found"));

        if (otp.getOtpCode().equals(otpDto.getOtp()) && !isOtpExpired(otp)) {
            otp.setOtpUsed(true);
            dataManager.save(
                    new SaveContext()
                            .removing(otp)
                            .setHint(PersistenceHints.SOFT_DELETION,false)
            );

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
        }
    }

    //initate password reset 1. find user 2. generate OTP 3. send email
    @RestMethod
    public ResponseEntity<String> initiatePasswordReset(String email) throws EmailException {
        log.info("Initiating password reset for email: {}", email);

        try {
            Optional<TathminiUser> userOptional = dataManager.load(TathminiUser.class)
                    .query("select m from TathminiUser m where m.email = :email")
                    .parameter("email", email)
                    .optional();

            log.info("Query executed. User found: {}", userOptional.isPresent());

            if (userOptional.isEmpty()) {
                log.warn("User not found for email: {}", email);
                return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
            }else {

                TathminiUser user = userOptional.get();
                log.info("Existing user: {}", user);

                int otpCode = otpGenerator();
                OTP otp = dataManager.create(OTP.class);
                otp.setOtpUsed(false);
                otp.setOtpCode(otpCode);
                otp.setPurpose("password_reset");
                otp.setTathminiUser(user);
                otp.setExpiritationTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));

                String emailBodyTemplate = "Your OTP for password reset is: %d";
                sendOtpToUser(user.getEmail(), otpCode, emailBodyTemplate);

                log.info("OTP created and email sent for user: {}", user.getEmail());
                dataManager.save(otp);


                return new ResponseEntity<>("OTP sent, please check your email", HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Error during password reset initiation for email: {}", email, e);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //update password
    @RestMethod
    public ResponseEntity<String> updatePassword(String email, String newPassword) {
        try {
            TathminiUser user = dataManager.load(TathminiUser.class)
                    .query("select m from TathminiUser m where m.email = :email")
                    .parameter("email", email)
                    .optional()
                    .orElseThrow(() -> new ResourceNotFoundExeption("Email not found"));


            user.setPassword(newPassword);
            dataManager.save(user);
            return new ResponseEntity<>("Password updated", HttpStatus.OK);
        } catch (ResourceNotFoundExeption e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isOtpExpired(OTP otp) {
        log.debug("Entering isOtpExpired method");
        try {
            Date expirationTime = otp.getExpiritationTime();
            log.debug("Expiration time: " + expirationTime);
            log.debug("Expiration time class: " + (expirationTime != null ? expirationTime.getClass().getName() : "null"));

            if (expirationTime == null) {
                log.debug("Expiration time is null, considering OTP expired");
                return true;
            }

            boolean isExpired = expirationTime.before(new Date());
            log.debug("Is OTP expired? " + isExpired);
            return isExpired;
        } catch (Exception e) {
            log.error("Exception in isOtpExpired method", e);
            return true; // Assume expired on error
        } finally {
            log.debug("Exiting isOtpExpired method");
        }
    }



    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

}
