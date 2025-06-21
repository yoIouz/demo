package by.test.sample.utils;

import by.test.sample.exception.LocalException;
import by.test.sample.repository.EmailDataRepository;
import by.test.sample.repository.PhoneDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static by.test.sample.utils.ApplicationConstants.EMAIL_TAKEN_MESSAGE;
import static by.test.sample.utils.ApplicationConstants.PHONE_TAKEN_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final EmailDataRepository emailDataRepository;

    private final PhoneDataRepository phoneDataRepository;

    public void validateEmailsAreUnique(Long currentUserId, List<String> emails) {
        if (emails != null && !emails.isEmpty()) {
            emails.stream()
                    .filter(email -> emailDataRepository.existsByEmailAndUserIdNot(email, currentUserId))
                    .findFirst()
                    .ifPresent(email -> {
                        log.error("Email {} already exists", email);
                        throw new LocalException(EMAIL_TAKEN_MESSAGE);
                    });
        }
    }

    public void validatePhonesAreUnique(Long currentUserId, List<String> phones) {
        if (phones != null && !phones.isEmpty()) {
            phones.stream()
                    .filter(phone -> phoneDataRepository.existsByPhoneAndUserIdNot(phone, currentUserId))
                    .findFirst()
                    .ifPresent(phone -> {
                        log.error("Phone {} already exists", phone);
                        throw new LocalException(PHONE_TAKEN_MESSAGE);
                    });
        }
    }
}
