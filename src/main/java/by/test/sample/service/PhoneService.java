package by.test.sample.service;

import by.test.sample.dto.PhoneDto;

public interface PhoneService {

    void addPhones(Long currentUserId, PhoneDto phones);
}
