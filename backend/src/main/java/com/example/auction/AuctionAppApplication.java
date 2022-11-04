package com.example.auction;

import com.example.auction.exceptions.EmailAlreadyExistsException;
import com.example.auction.model.ConfirmationToken;
import com.example.auction.model.Role;
import com.example.auction.model.User;
import com.example.auction.service.confirmationtoken.ConfirmationTokenService;
import com.example.auction.service.role.RoleService;
import com.example.auction.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class AuctionAppApplication implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public AuctionAppApplication(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuctionAppApplication.class, args);

    }

    @Override
    public void run(String... args) throws EmailAlreadyExistsException {
        insertPredefinedAdmin();
    }

    private void insertPredefinedAdmin() throws EmailAlreadyExistsException {
        String adminData = "admin";
        String adminPhoneNumber = "1234567890";
        String adminEmail = "auctionshunters@gmail.com";
        String adminCityAddress = "Caracal";
        String adminCreditCardNumber = "1234567890123456";

        if (!userService.isUserEmailAlreadyRegistered(adminEmail)) {

            Role role = new Role("ADMIN");
            roleService.save(role);
            Set<Role> set = new HashSet<>();
            set.add(role);

            User admin = new User(adminData, passwordEncoder.encode(adminData), adminEmail, adminCityAddress,
                    adminPhoneNumber, set, adminCreditCardNumber);
            admin.setLocked(false);
            admin.setEnabled(true);

            userService.save(admin);

            ConfirmationToken adminConfirmationToken = new ConfirmationToken(adminData,
                    LocalDateTime.of(LocalDate.of(2022, 2, 2), LocalTime.of(10, 11, 19)),
                    LocalDateTime.of(LocalDate.of(2022, 2, 2), LocalTime.of(10, 11, 19)),
                    LocalDateTime.of(LocalDate.of(2022, 2, 2), LocalTime.of(10, 11, 19)),
                    admin);

            confirmationTokenService.saveConfirmationToken(adminConfirmationToken);
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
