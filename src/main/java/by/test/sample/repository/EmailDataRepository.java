package by.test.sample.repository;

import by.test.sample.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmailData e " +
            "WHERE e.email = :email " +
            "AND e.user.id != :userId")
    boolean existsByEmailAndUserIdNot(@Param("email") String email, @Param("userId") Long userId);

}
