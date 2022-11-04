package com.example.auction.controller;


import com.example.auction.exceptions.EmailAlreadyExistsException;
import com.example.auction.exceptions.InvalidEmailException;
import com.example.auction.exceptions.WeakPasswordException;
import com.example.auction.model.Role;
import com.example.auction.model.User;
import com.example.auction.service.confirmationtoken.ConfirmationTokenService;
import com.example.auction.service.role.RoleService;
import com.example.auction.service.user.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:4200/")
@Validated
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, ConfirmationTokenService confirmationTokenService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping("")
    @ResponseStatus(CREATED)
    public User saveUser(@RequestBody @Valid User user) {

        Role role = new Role("CLIENT");
        roleService.save(role);
        Set<Role> set = new HashSet<>();
        set.add(role);

        user.setEnabled(true);
        user.setLocked(false);
        user.setRole(set);

        return userService.save(user);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> findUserById(@NotNull @PathVariable("id") Integer id) {
        Optional<User> user = Optional.ofNullable(userService.findById(id));

        if (user.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(user.get(), OK);
    }

    @GetMapping("/email")
    public ResponseEntity<User> findUserByEmail(@NotBlank @RequestParam(value = "email") String email) {
        Optional<User> user = Optional.ofNullable(userService.findByEmail(email));

        if (user.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUserById(@NotNull @PathVariable("id") Integer id) {
        confirmationTokenService.delete(id);
        userService.deleteById(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser, @NotNull @PathVariable Integer id) {
        userService.update(newUser, id);
        return new ResponseEntity<>(OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@Valid @RequestBody User userData) {
        Optional<User> optionalUser = Optional.ofNullable(userService.findByEmail(userData.getUsername()));

        if (optionalUser.isPresent() && passwordEncoder.matches(userData.getPassword(), optionalUser.get().getPassword())) {   //verify if the plain text password match the encoded password from DB
            return new ResponseEntity<>(OK);
        }

        throw new UsernameNotFoundException("Username: " + userData.getUsername() + " not found!");
    }

//    @PostMapping
//    public String register(@Valid @RequestBody User request) throws InvalidEmailException, EmailAlreadyExistsException, WeakPasswordException {
//        return userService.register(request);
//    }

    @GetMapping("/confirm")
    public String confirm(@NotBlank @RequestParam("token") String token) {
        return userService.confirmToken(token);
    }
}
