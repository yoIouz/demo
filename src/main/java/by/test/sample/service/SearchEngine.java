package by.test.sample.service;

import by.test.sample.dto.PageDto;
import by.test.sample.enums.SearchEngineType;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import org.springframework.data.domain.Pageable;

public interface SearchEngine {

    PageDto<UserDto> findAllUsers(Pageable pageable);

    PageDto<UserDto> searchUsers(UserFilter filter, Pageable pageable);

    UserDto findUserById(Long userId);

    SearchEngineType getType();
}
