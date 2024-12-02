package com.learnings.iot_platform.migrations;

import com.learnings.iot_platform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InsertUsersToDatabaseChangelogTest {

    private InsertUsersToDatabaseChangelog changelog;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        changelog = new InsertUsersToDatabaseChangelog(mongoTemplate, passwordEncoder);
    }

    @Test
    void testExecute_AdminNotExists_ShouldInsertAdmin(){
        when(mongoTemplate.findOne(any(Query.class), eq(User.class)))
                .thenReturn(null);

        String rawPassword = "password";
        String encodedPassword = "encoded-password-hash";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        changelog.execute();

        verify(mongoTemplate).findOne(
                argThat(query -> query.getQueryObject().get("username").equals("user1")),
                eq(User.class)
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mongoTemplate).insert(userCaptor.capture());

        User insertedUser = userCaptor.getValue();

        assertEquals("user1", insertedUser.getUsername());
        assertEquals(encodedPassword, insertedUser.getPasswordHash());
        assertEquals("user1@gmail.com", insertedUser.getEmail());
        assertTrue(insertedUser.getRoles().contains("ROLE_USER"));
        assertNotNull(insertedUser.getCreatedAt());
    }

    @Test
    void testExecute_UserAlreadyExists_ShouldNotInsertUser(){
        User existingUser = new User();
        existingUser.setUsername("user1");
        when(mongoTemplate.findOne(any(Query.class), eq(User.class)))
                .thenReturn(existingUser);

        changelog.execute();

        verify(mongoTemplate, never()).insert(any(User.class));
    }

    @Test
    void testRollback_ShouldRemoveUser() {
        changelog.rollback();

        verify(mongoTemplate).remove(
                argThat(query -> query.getQueryObject().get("username").equals("user1")),
                eq(User.class)
        );
    }

    @Test
    void testPasswordEncoding() {
        when(mongoTemplate.findOne(any(Query.class), eq(User.class))).thenReturn(null);

        String rawPassword = "password";
        String encodedPassword = "encoded-password-hash";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        changelog.execute();

        verify(passwordEncoder).encode(rawPassword);
    }

}
