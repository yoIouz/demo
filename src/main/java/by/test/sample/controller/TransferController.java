package by.test.sample.controller;

import by.test.sample.dto.TransferRequestDto;
import by.test.sample.service.TransferService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static by.test.sample.utils.ApplicationConstants.TRANSFER_SUCCESSFUL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<String> transfer(@AuthenticationPrincipal Long userId,
                                           @RequestBody @Valid TransferRequestDto requestDto) {
        transferService.transfer(userId, requestDto);
        return ResponseEntity.ok(TRANSFER_SUCCESSFUL);
    }
}
