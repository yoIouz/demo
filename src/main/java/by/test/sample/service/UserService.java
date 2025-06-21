package by.test.sample.service;

import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {

    PageDto<UserDto> findAllUsers(Pageable pageable);

    PageDto<UserDto> searchUsers(UserFilter userFilter, Pageable pageable);

    UserDto updateUser(Long currentUserId, UserDto userDto);
}
