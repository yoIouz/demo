package by.test.sample.service;

import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageDto<UserDto> findAllUsers(Pageable pageable);

    UserDto findUserById(Long userId);

    PageDto<UserDto> searchUsers(UserFilter userFilter, Pageable pageable);

    UserDto updateUser(Long currentUserId, UserDto userDto);
}
