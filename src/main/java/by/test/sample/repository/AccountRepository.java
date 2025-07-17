package by.test.sample.repository;

import by.test.sample.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * FROM account WHERE user_id = :userId FOR UPDATE", nativeQuery = true)
    Optional<Account> findByUserIdLocked(@Param("userId") Long userId);

    @Query("SELECT a FROM Account a ORDER BY a.id")
    Stream<Account> findOrderedStream();
}
