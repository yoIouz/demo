package by.test.sample.scheduler;

import by.test.sample.entity.Account;
import by.test.sample.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static by.test.sample.utils.ApplicationConstants.INITIAL_BALANCE_MULTIPLIER;
import static by.test.sample.utils.ApplicationConstants.MAX_INITIAL_BALANCE_MULTIPLIER;
import static by.test.sample.utils.ApplicationConstants.REDIS_INITIAL_BALANCES_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceScheduler {

    private final AccountRepository accountRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedDelay = 30_000)
    public void increaseBalance() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            String accountId = account.getId().toString();
            BigDecimal initialBalance;
            String cached = (String) redisTemplate.opsForHash().get(REDIS_INITIAL_BALANCES_KEY, accountId);

            if (cached != null) {
                initialBalance = new BigDecimal(cached);
            } else {
                initialBalance = account.getBalance();
                redisTemplate.opsForHash().putIfAbsent(REDIS_INITIAL_BALANCES_KEY,
                        accountId,
                        initialBalance.toPlainString());
            }
            BigDecimal maxAllowed = initialBalance.multiply(MAX_INITIAL_BALANCE_MULTIPLIER);

            BigDecimal currentBalance = account.getBalance();
            BigDecimal increment = currentBalance.multiply(INITIAL_BALANCE_MULTIPLIER);
            BigDecimal newBalance = currentBalance.add(increment);

            if (newBalance.compareTo(maxAllowed) > 0) {
                log.info("Balance exceeded max allowed for account ID = {}", accountId);
                continue;
            }
            account.setBalance(newBalance);
            log.info("Balance updated for account ID = {}: {} → {}", account.getId(),
                    currentBalance.setScale(2, RoundingMode.HALF_UP),
                    newBalance.setScale(2, RoundingMode.HALF_UP));
        }
        accountRepository.saveAll(accounts);
    }
}
