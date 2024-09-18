package com.company.tathminiv2.rest.controller;

import com.company.tathminiv2.entity.TathminiUser;
import com.company.tathminiv2.repository.TathminiUserRepository;
import com.company.tathminiv2.rest.config.JwtService;
import com.company.tathminiv2.rest.dto.LoginDto;
import com.company.tathminiv2.rest.dto.OtpDto;
import com.company.tathminiv2.rest.dto.RegisterDto;
import com.company.tathminiv2.rest.dto.UserResponseDto;
import com.company.tathminiv2.rest.services.TathminiUserService;
import io.jmix.email.EmailException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class TathminiUserController {
    private final TathminiUserRepository tathminiUserRepository;
    private static final Logger log = LoggerFactory.getLogger(TathminiUserController.class);

    @Autowired
    private TathminiUserService memberService;

    @Autowired
    private JwtService jwtService;

    public TathminiUserController(TathminiUserRepository tathminiUserRepository) {
        this.tathminiUserRepository = tathminiUserRepository;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {

        try {
            log.info(registerDto.toString());
            String responseMessage = memberService.registerService(registerDto);
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


@GetMapping()
public ResponseEntity<Page<TathminiUser>> getUsers(Pageable pageable) {
    return ResponseEntity.ok(tathminiUserRepository.findAll(pageable));
}
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto) {
        String responseMessage = memberService.login(loginDto);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpDto otpDto) {
        ResponseEntity<?> verificationResult = memberService.verifyOtp(otpDto);

        if (verificationResult.getStatusCode().is2xxSuccessful()) {
            TathminiUser tathminiUser = (TathminiUser) verificationResult.getBody();

            UserResponseDto userResponseDto = new UserResponseDto();
            userResponseDto.setId(tathminiUser.getId());
            userResponseDto.setUsername(tathminiUser.getUsername());
            userResponseDto.setFirstName(tathminiUser.getFirstName());
            userResponseDto.setLastName(tathminiUser.getLastName());
            userResponseDto.setEmail(tathminiUser.getEmail());
            userResponseDto.setDob(tathminiUser.getDob());
            userResponseDto.setPhoneNumber(tathminiUser.getPhoneNumber());
            userResponseDto.setRiskProfile(tathminiUser.getRiskProfile());
            userResponseDto.setInvestorStatus(tathminiUser.getInvestorStatus());

            String token = jwtService.generateToken(tathminiUser);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userResponseDto);

            return ResponseEntity.ok(response);
        } else {
            return verificationResult;
        }
    }



    @PostMapping("/initiate-password-reset")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam String email) throws EmailException, EmailException {
        return memberService.initiatePasswordReset(email);
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String email, @RequestParam String newPassword) {
        return memberService.updatePassword(email, newPassword);
    }
}
