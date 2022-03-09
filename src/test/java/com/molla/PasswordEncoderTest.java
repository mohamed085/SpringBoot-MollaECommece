package com.molla;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {

    @Test
    public void testEncodePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "MO0420sara";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println(encodedPassword);
        System.out.println(passwordEncoder.matches("MO0420sara", encodedPassword));
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        assertThat(matches).isTrue();
    }
}
