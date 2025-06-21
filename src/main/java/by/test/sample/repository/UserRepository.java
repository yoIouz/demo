package by.test.sample.repository;

import by.test.sample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("""
                SELECT u FROM User u
                WHERE EXISTS (
                    SELECT 1 FROM EmailData e
                    WHERE e.email = :identity AND e.user = u
                ) OR EXISTS (
                    SELECT 1 FROM PhoneData p
                    WHERE p.phone = :identity AND p.user = u
                )
            """)
    Optional<User> findIdentity(@Param("identity") String identity);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.emails LEFT JOIN FETCH u.phones WHERE u.id = :id")
    Optional<User> findWithRelationsById(@Param("id") Long id);

}
