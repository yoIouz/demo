package by.test.sample.service.impl;

import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import by.test.sample.entity.User;
import by.test.sample.enums.SearchEngineType;
import by.test.sample.exception.UserNotFoundException;
import by.test.sample.mapper.UserMapper;
import by.test.sample.repository.UserRepository;
import by.test.sample.repository.UserSpecifications;
import by.test.sample.service.SearchEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JPASearchEngine implements SearchEngine {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageDto<UserDto> findAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        if (!usersPage.hasContent()) {
            throw new UserNotFoundException();
        }
        return userMapper.toUserDtoPage(usersPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<UserDto> searchUsers(UserFilter filter, Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(UserSpecifications.withFilter(filter), pageable);
        if (!usersPage.hasContent()) {
            throw new UserNotFoundException();
        }
        return userMapper.toUserDtoPage(usersPage);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public SearchEngineType getType() {
        return SearchEngineType.JPA;
    }
}
