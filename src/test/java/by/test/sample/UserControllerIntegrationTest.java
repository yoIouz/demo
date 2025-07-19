package by.test.sample;

import by.test.sample.configuration.TestCacheConfiguration;
import by.test.sample.configuration.TestSecurityConfiguration;
import by.test.sample.dto.UserFilter;
import by.test.sample.testcontainers.CustomTestContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfiguration.class, TestCacheConfiguration.class})
public class UserControllerIntegrationTest extends CustomTestContainer {

    private static final String API_URL = "/api/users";

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    public void testFindAllUsers() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    public void testSearchUsersValidFilterReturnsOk() throws Exception {
        UserFilter filter = new UserFilter();
        filter.setPhone("79201234567");
        String jsonFilter = objectMapper.writeValueAsString(filter);
        mockMvc.perform(post(API_URL + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilter)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[?(@.name == 'Иван Иванов')]").exists());
    }

    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    public void testSearchUsersInvalidFilterReturnsBadRequest() throws Exception {
        UserFilter filter = new UserFilter();
        filter.setEmail("email");
        String jsonFilter = objectMapper.writeValueAsString(filter);

        mockMvc.perform(post(API_URL + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilter))
                .andExpect(status().isBadRequest());
    }
}
