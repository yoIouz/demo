package by.test.sample.service;

import by.test.sample.dto.TransferRequestDto;

public interface TransferService {

    void transfer(Long fromUserId, TransferRequestDto requestDto);
}
