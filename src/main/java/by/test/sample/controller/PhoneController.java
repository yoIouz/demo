package by.test.sample.controller;

import by.test.sample.dto.PhoneDto;
import by.test.sample.service.PhoneService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/phones")
@SecurityRequirement(name = "bearerAuth")
public class PhoneController {

    private final PhoneService phoneService;

    @PostMapping("/add")
    public ResponseEntity<Void> addPhone(@AuthenticationPrincipal Long currentUserId,
                                         @RequestBody @Valid PhoneDto phones) {
        phoneService.addPhones(currentUserId, phones);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

}
