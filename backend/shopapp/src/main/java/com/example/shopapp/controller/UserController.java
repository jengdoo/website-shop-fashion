package com.example.shopapp.controller;

import com.example.shopapp.Model.Role;
import com.example.shopapp.Model.User;
import com.example.shopapp.response.LoginResponse;
import com.example.shopapp.response.RegisterResponse;
import com.example.shopapp.response.UserResponse;
import com.example.shopapp.service.UserService;
import com.example.shopapp.components.LocalizationUtil;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.example.shopapp.dto.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userService;
    private final LocalizationUtil localizationUtil;
    @PostMapping("/register")
    public ResponseEntity<?> createRegister(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> messageError = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageError);
            }
            if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
                return ResponseEntity.badRequest().body(localizationUtil.getLocalizedMessage(MessageKeys.REGISTER_FAILED));
            }
            User user = userService.createUser(userDTO);
//            return ResponseEntity.ok("register successfully");
            return ResponseEntity.ok(new RegisterResponse("Register successfully",user));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        try {
            String token  = userService.login(userLoginDTO.getPhoneNumber(),userLoginDTO.getPassword());
            String message= localizationUtil.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY);
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(message)
                    .token(token)
                    .build());
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(LoginResponse.builder().message(localizationUtil.getLocalizedMessage(MessageKeys.LOGIN_FAILED,e.getMessage())).build());
        }
    }
    @PostMapping("/details")
    public ResponseEntity<?> getUserDetail(@RequestHeader("Authorization") String token){
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if (user != null && (userService.hasRole(user, "USER") || userService.hasRole(user, "ADMIN"))) {
                return ResponseEntity.ok(UserResponse.convertUserResponse(user));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(localizationUtil.getLocalizedMessage(MessageKeys.ACCOUNT_HAVE_NOT_ACCESS));
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable long userId){
        try {
            User user = userService.userFindById(userId);
            return ResponseEntity.ok(UserResponse.convertUserResponse(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

