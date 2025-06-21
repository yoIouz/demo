package by.test.sample.service.impl;

import by.test.sample.dto.TransferRequestDto;
import by.test.sample.entity.Account;
import by.test.sample.exception.AccountNotFoundException;
import by.test.sample.exception.TransferException;
import by.test.sample.repository.AccountRepository;
import by.test.sample.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static by.test.sample.utils.ApplicationConstants.BALANCE_NEGATIVE_MESSAGE;
import static by.test.sample.utils.ApplicationConstants.SELF_TRANSFER_MESSAGE;
import static by.test.sample.utils.ApplicationConstants.TRANSFER_NEGATIVE_MESSAGE;
import static by.test.sample.utils.ApplicationConstants.TRANSFER_REQUEST_MISSING_MESSAGE;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void transfer(Long fromUserId, TransferRequestDto requestDto) {
        if (requestDto == null) {
            throw new TransferException(TRANSFER_REQUEST_MISSING_MESSAGE);
        }
        Long toUserId = requestDto.toUserId();
        BigDecimal amount = requestDto.amount();

        if (fromUserId.equals(toUserId)) {
            throw new TransferException(SELF_TRANSFER_MESSAGE);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException(TRANSFER_NEGATIVE_MESSAGE);
        }

        Long firstLockId = fromUserId < toUserId ? fromUserId : toUserId;
        Long secondLockId = fromUserId < toUserId ? toUserId : fromUserId;

        Account firstAccount = accountRepository.findByUserIdLocked(firstLockId)
                .orElseThrow(() -> new AccountNotFoundException(firstLockId));
        Account secondAccount = accountRepository.findByUserIdLocked(secondLockId)
                .orElseThrow(() -> new AccountNotFoundException(secondLockId));

        if (fromUserId.equals(firstLockId)) {
            this.decreaseBalance(firstAccount, amount);
            this.increaseBalance(secondAccount, amount);
        } else {
            this.decreaseBalance(secondAccount, amount);
            this.increaseBalance(firstAccount, amount);
        }

        accountRepository.save(firstAccount);
        accountRepository.save(secondAccount);
    }

    private void increaseBalance(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
    }

    private void decreaseBalance(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransferException(BALANCE_NEGATIVE_MESSAGE);
        }
        account.setBalance(newBalance);
    }
}
