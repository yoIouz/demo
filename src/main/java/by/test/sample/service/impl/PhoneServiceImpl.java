package by.test.sample.service.impl;

import by.test.sample.dto.PhoneDto;
import by.test.sample.entity.PhoneData;
import by.test.sample.exception.UserNotFoundException;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.PhoneService;
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
public class PhoneServiceImpl implements PhoneService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    @Override
    @Transactional
    @CacheEvict(value = "userSearchCache", allEntries = true)
    public void addPhones(Long currentUserId, PhoneDto phones) {
        var user = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        userValidator.validatePhonesAreUnique(currentUserId, phones.phones());
        Set<String> existingPhones = user.getPhones().stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());

        List<PhoneData> newPhones = phones.phones().stream()
                .filter(phone -> !existingPhones.contains(phone))
                .map(phone -> {
                    PhoneData phoneData = new PhoneData();
                    phoneData.setPhone(phone);
                    phoneData.setUser(user);
                    return phoneData;
                })
                .toList();
        user.getPhones().addAll(newPhones);
        userRepository.save(user);
    }
}
