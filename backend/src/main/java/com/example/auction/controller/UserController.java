package com.example.auction.controller;


import com.example.auction.exceptions.EmailAlreadyExistsException;
import com.example.auction.exceptions.InvalidEmailException;
import com.example.auction.exceptions.WeakPasswordException;
import com.example.auction.model.Role;
import com.example.auction.model.User;
import com.example.auction.service.confirmationtoken.ConfirmationTokenService;
import com.example.auction.service.role.RoleService;
import com.example.auction.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
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

    public UserController(UserService userService,
                          ConfirmationTokenService confirmationTokenService,
                          PasswordEncoder passwordEncoder,
                          RoleService roleService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Operation(summary = "Get a list with all the users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

//    @PostMapping("")
//    @Operation(summary = "Save a user in the database")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "User successfully inserted in the database",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(implementation = User.class))}),
//
//    })
//    @ResponseStatus(CREATED)
//    public User saveUser(@RequestBody @Valid User user) {
//
//        Role role = new Role("CLIENT");
//        roleService.save(role);
//        Set<Role> set = new HashSet<>();
//        set.add(role);
//
//        user.setEnabled(true);
//        user.setLocked(false);
//        user.setRole(set);
//
//        return userService.save(user);
//    }

    @Operation(summary = "Get a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User founded",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),

    })
    @GetMapping("{id}")
    public ResponseEntity<User> findUserById(@NotNull @PathVariable("id") Integer id) {
        Optional<User> user = Optional.ofNullable(userService.findById(id));

        if (user.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(user.get(), OK);
    }

    @Operation(summary = "Get a user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User founded",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),

    })
    @GetMapping("/email")
    public ResponseEntity<User> findUserByEmail(@NotBlank @RequestParam(value = "email") String email) {
        Optional<User> user = Optional.ofNullable(userService.findByEmail(email));

        if (user.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @Operation(summary = "Delete a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Not found the event",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),

    })
    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUserById(@NotNull @PathVariable("id") Integer id) {
        confirmationTokenService.delete(id);
        userService.deleteById(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @Operation(summary = "Update a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details were updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User details were not updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),

    })
    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser, @NotNull @PathVariable Integer id) {
        userService.update(newUser, id);
        return new ResponseEntity<>(OK);
    }


    @Operation(summary = "Login a user in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "User could not perform logging due to bad credentials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),

    })
    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@Valid @RequestBody User userData) {
        Optional<User> optionalUser = Optional.ofNullable(userService.findByEmail(userData.getUsername()));

        if (optionalUser.isPresent() && passwordEncoder.matches(userData.getPassword(), optionalUser.get().getPassword())) {   //verify if the plain text password match the encoded password from DB
            return new ResponseEntity<>(OK);
        }

        return new ResponseEntity<>(UNAUTHORIZED);
    }

    @PostMapping
    public String register(@Valid @RequestBody User request) throws EmailAlreadyExistsException, InvalidEmailException, WeakPasswordException {
        return userService.register(request);
    }

    @Operation(summary = "Confirm token for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token was confirmed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Token was not confirmed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),

    })
    @GetMapping("/confirm")
    public String confirm(@NotBlank @RequestParam("token") String token) {
        return userService.confirmToken(token);
    }
}
