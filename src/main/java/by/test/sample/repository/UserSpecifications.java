package by.test.sample.repository;

import by.test.sample.dto.UserFilter;
import by.test.sample.entity.EmailData;
import by.test.sample.entity.PhoneData;
import by.test.sample.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecifications {

    public static Specification<User> withFilter(UserFilter filter) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            return Specification.where(dateOfBirthGreaterOrEqual(filter.getDateOfBirth()))
                    .and(nameStartsWithIgnoreCase(filter.getName()))
                    .and(emailEquals(filter.getEmail()))
                    .and(phoneEquals(filter.getPhone()))
                    .toPredicate(root, query, cb);
        };
    }

    private static Specification<User> dateOfBirthGreaterOrEqual(LocalDate dateOfBirth) {
        return (root, query, cb) -> {
            if (dateOfBirth == null) return null;
            return cb.greaterThanOrEqualTo(root.get("dateOfBirth"), dateOfBirth);
        };
    }

    private static Specification<User> nameStartsWithIgnoreCase(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%");
        };
    }

    private static Specification<User> emailEquals(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return null;
            Join<User, EmailData> emailJoin = root.join("emails", JoinType.LEFT);
            return cb.equal(cb.lower(emailJoin.get("email")), email.toLowerCase());
        };
    }

    private static Specification<User> phoneEquals(String phone) {
        return (root, query, cb) -> {
            if (phone == null || phone.isBlank()) return null;
            Join<User, PhoneData> phoneJoin = root.join("phones", JoinType.LEFT);
            return cb.equal(phoneJoin.get("phone"), phone);
        };
    }
}
