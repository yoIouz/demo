package by.test.sample.service;

import by.test.sample.dto.EmailDto;

public interface EmailService {

    void addEmail(Long currentUserId, EmailDto emails);
}
