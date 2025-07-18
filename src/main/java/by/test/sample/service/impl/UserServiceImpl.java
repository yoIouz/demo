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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(UserNotFoundException::new);
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
        User existing = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        userValidator.validateEmailsAreUnique(currentUserId, userDto.emails());
        userValidator.validatePhonesAreUnique(currentUserId, userDto.phones());

        existing.setName(userDto.name() == null ? existing.getName() : userDto.name());
        existing.setDateOfBirth(userDto.dateOfBirth() == null ? existing.getDateOfBirth() : userDto.dateOfBirth());
        existing.setPassword(userDto.password() == null ? existing.getPassword() : userDto.password());

        if (userDto.phones() != null && !userDto.phones().isEmpty()) {
            existing.getPhones().clear();
            existing.getPhones().addAll(
                    userDto.phones().stream()
                            .map(phone -> new PhoneData(null, phone, existing))
                            .toList()
            );
        }
        if (userDto.emails() != null && !userDto.emails().isEmpty()) {
            existing.getEmails().clear();
            existing.getEmails().addAll(
                    userDto.emails().stream()
                            .map(email -> new EmailData(null, email, existing))
                            .toList()
            );
        }
        userRepository.save(existing);
        return userMapper.toUserDto(existing);
    }

    private PageDto<UserDto> delegateToEngine(Function<SearchEngine, PageDto<UserDto>> operation) {
        SearchEngineType key = SearchEngineType.getEngine(useElastic);
        return Optional.ofNullable(searchEnginesMap.get(key))
                .map(operation)
                .orElseThrow(() -> new SearchEngineNotFoundException(key));
    }
}
