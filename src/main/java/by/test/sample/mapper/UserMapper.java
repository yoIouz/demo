package by.test.sample.mapper;

import by.test.sample.document.UserElasticDocument;
import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.entity.EmailData;
import by.test.sample.entity.PhoneData;
import by.test.sample.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

@Mapper
@SuppressWarnings("unused")
public interface UserMapper {

    @Mapping(target = "phones", source = "phones", qualifiedByName = "phonesToStrings")
    @Mapping(target = "emails", source = "emails", qualifiedByName = "emailsToStrings")
    UserDto toUserDto(User user);

    @Mapping(target = "phones", source = "phones", qualifiedByName = "phonesToStrings")
    @Mapping(target = "emails", source = "emails", qualifiedByName = "emailsToStrings")
    UserElasticDocument toUserElasticDto(User user);

    default PageDto<UserDto> toUserDtoPage(Page<User> usersPage) {
        return new PageDto<>(usersPage.map(this::toUserDto));
    }

    @Named("phonesToStrings")
    default List<String> phonesToStrings(Set<PhoneData> phones) {
        if (phones == null) return List.of();
        return phones.stream()
                .map(PhoneData::getPhone)
                .toList();
    }

    @Named("emailsToStrings")
    default List<String> emailsToStrings(Set<EmailData> emails) {
        if (emails == null) return List.of();
        return emails.stream()
                .map(EmailData::getEmail)
                .toList();
    }
}
