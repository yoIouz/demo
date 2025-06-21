package by.test.sample.repository;

import by.test.sample.entity.Account;
import by.test.sample.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * FROM account WHERE user_id = :userId FOR UPDATE", nativeQuery = true)
    Optional<Account> findByUserIdLocked(@Param("userId") Long userId);

}
