package by.test.sample.repository;

import by.test.sample.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PhoneData p " +
            "WHERE p.phone = :phone " +
            "AND p.user.id != :userId")
    boolean existsByPhoneAndUserIdNot(@Param("phone") String phone, @Param("userId") Long userId);

}
