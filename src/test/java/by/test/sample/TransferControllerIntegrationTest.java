package by.test.sample;

import by.test.sample.configuration.TestCacheConfiguration;
import by.test.sample.configuration.TestSecurityConfiguration;
import by.test.sample.dto.TransferRequestDto;
import by.test.sample.entity.Account;
import by.test.sample.entity.User;
import by.test.sample.exception.TransferException;
import by.test.sample.repository.AccountRepository;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.TransferService;
import by.test.sample.testcontainers.CustomTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityConfiguration.class, TestCacheConfiguration.class})
public class TransferControllerIntegrationTest extends CustomTestContainer {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    void testSuccessfulTransfer() {
        User fromUser = new User();
        fromUser.setName("Sender");
        userRepository.save(fromUser);

        User toUser = new User();
        toUser.setName("Receiver");
        userRepository.save(toUser);

        Account fromAccount = new Account();
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(new BigDecimal("100.00"));
        accountRepository.save(fromAccount);

        Account toAccount = new Account();
        toAccount.setUser(toUser);
        toAccount.setBalance(new BigDecimal("20.00"));
        accountRepository.save(toAccount);

        TransferRequestDto dto = new TransferRequestDto(toUser.getId(), new BigDecimal("60.00"));

        transferService.transfer(fromUser.getId(), dto);

        Account updatedFrom = accountRepository.findByUserIdLocked(fromUser.getId()).orElseThrow();
        Account updatedTo = accountRepository.findByUserIdLocked(toUser.getId()).orElseThrow();

        assertEquals(new BigDecimal("40.00").setScale(2, RoundingMode.UNNECESSARY),
                updatedFrom.getBalance());
        assertEquals(new BigDecimal("80.00").setScale(2, RoundingMode.UNNECESSARY),
                updatedTo.getBalance());
    }

    @Test
    void transferToSameUserShouldFail() {
        TransferRequestDto request = new TransferRequestDto(1L, new BigDecimal("100"));
        TransferException ex = assertThrows(TransferException.class,
                () -> transferService.transfer(1L, request));

        assertEquals("Cannot transfer to the same account", ex.getMessage());
    }

    @Test
    void negativeAmountShouldFail() {
        TransferRequestDto request = new TransferRequestDto(2L, new BigDecimal("-100"));
        TransferException ex = assertThrows(TransferException.class, () ->
                transferService.transfer(1L, request));

        assertEquals("Transfer amount must be positive", ex.getMessage());
    }

    @Test
    void insufficientBalanceShouldFail() {
        Account account = accountRepository.findByUserIdLocked(1L).orElseThrow();
        account.setBalance(new BigDecimal("0"));
        accountRepository.save(account);

        TransferRequestDto request = new TransferRequestDto(2L, new BigDecimal("50"));
        TransferException ex = assertThrows(TransferException.class,
                () -> transferService.transfer(1L, request));

        assertEquals("Balance cannot be negative", ex.getMessage());
    }
}
