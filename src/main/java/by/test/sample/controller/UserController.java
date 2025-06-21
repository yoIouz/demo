package by.test.sample.controller;

import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import by.test.sample.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageDto<UserDto>> findUsers(@ParameterObject
                                                      @PageableDefault(size = 20) Pageable pageable) {
        PageDto<UserDto> users = userService.findAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/search")
    public ResponseEntity<PageDto<UserDto>> searchUsers(@RequestBody @Valid UserFilter userFilter,
                                                        @ParameterObject
                                                        @PageableDefault(size = 20) Pageable pageable) {
        PageDto<UserDto> users = userService.searchUsers(userFilter, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal Long currentUserId,
                                              @RequestBody @Valid UserDto userDto) {
        UserDto updatedUser = userService.updateUser(currentUserId, userDto);
        return ResponseEntity.ok(updatedUser);
    }
}
