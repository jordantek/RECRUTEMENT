package com.tpc.tpcgestpaie.localapp.controller.users;

import com.tpc.tpcgestpaie.localapp.dto.UserDTO;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserInfosController {

    private final UserService userService;

    public UserInfosController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        try {
            UserDTO userDTO = userService.getCurrentUserDTO();
            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré", userDTO));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
//        Optional<User> user = userService.findById(id);
//        UserDTO userDTO = new UserDTO(user);
//        return ResponseEntity.ok(userDTO);
//    }

//    @GetMapping
//    public ResponseEntity<List<UserDTO>> getAllUsers() {
//        List<User> users = userService.getAllUsers();
//        List<UserDTO> userDTOs = users.stream()
//                .map(UserDTO::new)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(userDTOs);
//    }
}
