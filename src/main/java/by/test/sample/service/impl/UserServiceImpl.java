package by.test.sample.service.impl;

import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import by.test.sample.entity.EmailData;
import by.test.sample.entity.PhoneData;
import by.test.sample.entity.User;
import by.test.sample.enums.SearchEngineType;
import by.test.sample.exception.SearchEngineNotFoundException;
import by.test.sample.exception.UserNotFoundException;
import by.test.sample.mapper.UserMapper;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.SearchEngine;
import by.test.sample.service.UserService;
import by.test.sample.utils.UserValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Value("${search.use-elastic:false}")
    private boolean useElastic;

    private final Map<SearchEngineType, SearchEngine> searchEnginesMap;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserValidator userValidator;

    public UserServiceImpl(List<SearchEngine> searchEngines, UserRepository userRepository,
                           UserMapper userMapper, UserValidator userValidator) {
        this.searchEnginesMap = searchEngines.stream()
                .collect(Collectors.toMap(SearchEngine::getType, Function.identity()));
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Override
    public PageDto<UserDto> findAllUsers(Pageable pageable) {
        return this.delegateToEngine(engine -> engine.findAllUsers(pageable));
    }

    @Override
    @Cacheable(value = "userCache", key = "#userId")
    public UserDto findUserById(Long userId) {
        return this.delegateToEngine(engine -> engine.findUserById(userId));
    }

    @Override
    @Cacheable(value = "userSearchCache", keyGenerator = "userKeyGenerator", unless = "#result.isEmpty()")
    public PageDto<UserDto> searchUsers(UserFilter userFilter, Pageable pageable) {
        return this.delegateToEngine(engine -> engine.searchUsers(userFilter, pageable));
    }

    @Override
    @Transactional
    @CachePut(value = "userCache", key = "#currentUserId")
    public UserDto updateUser(Long currentUserId, UserDto userDto) {
        User existingUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        userValidator.validateEmailsAreUnique(currentUserId, userDto.emails());
        userValidator.validatePhonesAreUnique(currentUserId, userDto.phones());

        existingUser.setName(userDto.name() == null ?
                existingUser.getName() : userDto.name());
        existingUser.setDateOfBirth(userDto.dateOfBirth() == null ?
                existingUser.getDateOfBirth() : userDto.dateOfBirth());

        if (userDto.phones() != null && !userDto.phones().isEmpty()) {
            existingUser.getPhones().clear();
            existingUser.getPhones().addAll(
                    userDto.phones().stream()
                            .map(phone -> new PhoneData(null, phone, existingUser))
                            .toList()
            );
        }
        if (userDto.emails() != null && !userDto.emails().isEmpty()) {
            existingUser.getEmails().clear();
            existingUser.getEmails().addAll(
                    userDto.emails().stream()
                            .map(email -> new EmailData(null, email, existingUser))
                            .toList()
            );
        }
        userRepository.save(existingUser);
        return userMapper.toUserDto(existingUser);
    }

    private <T> T delegateToEngine(Function<SearchEngine, T> operation) {
        SearchEngineType key = SearchEngineType.getEngine(useElastic);
        return Optional.ofNullable(searchEnginesMap.get(key))
                .map(operation)
                .orElseThrow(() -> new SearchEngineNotFoundException(key));
    }
}
