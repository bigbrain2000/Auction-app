package com.example.auction.service.user;


import com.example.auction.exceptions.EmailAlreadyExistsException;
import com.example.auction.exceptions.InvalidEmailException;
import com.example.auction.exceptions.ResourceNotFoundException;
import com.example.auction.exceptions.WeakPasswordException;
import com.example.auction.model.ConfirmationToken;
import com.example.auction.model.User;
import com.example.auction.repository.UserRepository;
import com.example.auction.service.confirmationtoken.ConfirmationTokenService;
import com.example.auction.service.email.EmailService;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.now;
import static java.util.List.of;

/**
 * Service class for managing users and implementing the {@link UserService} interface.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           ConfirmationTokenService confirmationTokenService,
                           EmailService emailService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            LOGGER.debug("The user's list was empty and users could not be retrieved from the database.");
            return of();
        }

        LOGGER.debug("The user's list was not empty and users were successfully retrieved from the database.");
        return new ArrayList<>(userList);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Could not find the user by the id {} ", id);
            return new ResourceNotFoundException("User", "id", id);
        });
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            LOGGER.debug("Could not find the user identified by email {} ", email);
            return new ResourceNotFoundException("User", "email", email);
        });
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        LOGGER.debug("User {} saved in the database.", user);
        return userRepository.save(user);
    }

    @Override
    public User update(@NotNull User newUser, Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Could not find the user by the id {} ", id);
            return new ResourceNotFoundException("User", "id", id);
        });

        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());
        user.setCityAddress(newUser.getCityAddress());
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.setRole(newUser.getRole());
        user.setCreditCardNumber(newUser.getCreditCardNumber());

        LOGGER.debug("Token successfully updated in the database");
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Token could not be deleted from the database");
            return new ResourceNotFoundException("Token", "id", id);
        });

        LOGGER.debug("Token successfully deleted from the database");
        userRepository.deleteById(id);
    }


    @Override
    public boolean isUserEmailAlreadyRegistered(String username) {
        boolean userIsFound = userRepository.findByEmail(username).isPresent();

        if (!userIsFound) {
            LOGGER.debug("User {} was not found by email", username);
        }

        return userIsFound;
    }

    /**
     * Check if a user email already exists.
     *
     * @param email - email of user
     * @throws EmailAlreadyExistsException - if a user with the same username already exists
     */
    public void checkIfEmailAlreadyExists(String email) throws EmailAlreadyExistsException {
        if (userRepository.findByEmail(email).isPresent()) {
            LOGGER.debug("User email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }
    }

    @Override
    public void checkIfEmailIsValid(String email) throws InvalidEmailException {
        if (!isValidEmailAddress(email) || email.isEmpty()) {
            LOGGER.debug("User email {} is not valid", email);
            throw new InvalidEmailException(email);
        }
    }

    private boolean isValidEmailAddress(String email) {

        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }

        return result;
    }

    public void checkPasswordFormat(String password) throws WeakPasswordException {
        checkIfPasswordContainsAtLeast8Characters(password);
        checkIfPasswordContainsAtLeast1Digit(password);
        checkIfPasswordContainsAtLeast1UpperCase(password);
    }

    private void checkIfPasswordContainsAtLeast8Characters(@NotNull String password) throws WeakPasswordException {
        if (password.length() < 8)
            throw new WeakPasswordException("8 characters");
    }

    private void checkIfPasswordContainsAtLeast1Digit(String password) throws WeakPasswordException {
        if (!stringContainsNumber(password))
            throw new WeakPasswordException("one digit");
    }

    public boolean stringContainsNumber(String s) {
        return Pattern.compile("\\d").matcher(s).find();
    }

    private void checkIfPasswordContainsAtLeast1UpperCase(String password) throws WeakPasswordException {
        if (!stringContainsUpperCase(password))
            throw new WeakPasswordException("one upper case");
    }

    public boolean stringContainsUpperCase(String s) {
        return Pattern.compile("[A-Z]").matcher(s).find();
    }

    private String signUpUser(@NotNull User appUser) throws EmailAlreadyExistsException, WeakPasswordException {

        checkIfEmailAlreadyExists(appUser.getEmail());
        checkPasswordFormat(appUser.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setEnabled(true);
        appUser.setLocked(false);
        userRepository.save(appUser);
        LOGGER.debug("User {} inserted into the database", appUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                now(),
                now().plusMinutes(30),
                now(),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        LOGGER.debug("Token {} for user {} inserted into the database", confirmationToken, appUser);

        return token;
    }

    @Override
    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }

    @Override
    public String register(@NotNull User newUser) throws InvalidEmailException, EmailAlreadyExistsException, WeakPasswordException {

        //checkIfEmailIsValid(newUser.getEmail());

        String token = signUpUser(
                new User(
                        newUser.getUsername(),
                        newUser.getPassword(),
                        newUser.getEmail(),
                        newUser.getCityAddress(),
                        newUser.getPhoneNumber(),
                        newUser.getRole(),
                        newUser.getCreditCardNumber()
                )
        );

        String link = "http://localhost:8080/users/confirm?token=" + token; //url for validating user account

        emailService.send(newUser.getEmail(), buildEmail(newUser.getUsername(), link));
        LOGGER.debug("Email for confirmation token was sent.");

        return token;
    }

    @Transactional
    @Override
    public String confirmToken(String token) {

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> {
                    LOGGER.debug("Token was not found.");
                    return new IllegalStateException("Token was not found");
                });

        confirmationTokenService.setConfirmedAt(token);
        LOGGER.debug("Token confirmed by user.");

        enableUser(confirmationToken.getUser().getEmail());
        LOGGER.debug("User was enabled to access the app.");

        return "Email confirmed!";
    }

    @Contract(pure = true)
    private @NotNull String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a  target=\"_blank\" href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 30 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}

