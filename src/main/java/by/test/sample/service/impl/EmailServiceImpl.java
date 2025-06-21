package by.test.sample.service.impl;

import by.test.sample.dto.EmailDto;
import by.test.sample.entity.EmailData;
import by.test.sample.exception.UserNotFoundException;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.EmailService;
import by.test.sample.utils.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    @Override
    @Transactional
    @CacheEvict(value = "userSearchCache", allEntries = true)
    public void addEmail(Long currentUserId, EmailDto emails) {
        var user = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        userValidator.validateEmailsAreUnique(currentUserId, emails.emails());
        Set<String> existingEmails = user.getEmails().stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());

        List<EmailData> newEmails = emails.emails().stream()
                .filter(email -> !existingEmails.contains(email))
                .map(email -> {
                    EmailData emailData = new EmailData();
                    emailData.setEmail(email);
                    emailData.setUser(user);
                    return emailData;
                })
                .toList();
        user.getEmails().addAll(newEmails);
        userRepository.save(user);
    }
}
