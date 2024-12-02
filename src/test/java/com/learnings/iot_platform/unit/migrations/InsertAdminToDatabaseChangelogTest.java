package com.learnings.iot_platform.unit.migrations;

import com.learnings.iot_platform.migrations.InsertAdminToDatabaseChangelog;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InsertAdminToDatabaseChangelogTest {

    private InsertAdminToDatabaseChangelog changelog;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        changelog = new InsertAdminToDatabaseChangelog(mongoTemplate, passwordEncoder);
    }

    @Test
    void testExecute_AdminNotExists_ShouldInsertAdmin(){
        when(mongoTemplate.findOne(any(Query.class), eq(User.class)))
        .thenReturn(null);

        String rawPassword = "12121212";
        String encodedPassword = "encoded-password-hash";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        changelog.execute();

        verify(mongoTemplate).findOne(
                argThat(query -> query.getQueryObject().get("username").equals("admin1")),
                eq(User.class)
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mongoTemplate).insert(userCaptor.capture());

        User insertedUser = userCaptor.getValue();

        assertEquals("admin1", insertedUser.getUsername());
        assertEquals(encodedPassword, insertedUser.getPasswordHash());
        assertEquals("admin1@gmail.com", insertedUser.getEmail());
        assertTrue(insertedUser.getRoles().contains("ROLE_ADMIN"));
        assertNotNull(insertedUser.getCreatedAt());
    }

    @Test
    void testExecute_AdminAlreadyExists_ShouldNotInsertAdmin() {
        User existingAdmin = new User();
        existingAdmin.setUsername("admin1");
        when(mongoTemplate.findOne(any(Query.class), eq(User.class))).thenReturn(existingAdmin);


        changelog.execute();

        verify(mongoTemplate, never()).insert(any(User.class));
    }

    @Test
    void testRollback_ShouldRemoveAdminUser() {
        changelog.rollback();

        verify(mongoTemplate).remove(
                argThat(query -> query.getQueryObject().get("username").equals("admin1")),
                eq(User.class)
        );
    }

    @Test
    void testPasswordEncoding() {
        when(mongoTemplate.findOne(any(Query.class), eq(User.class))).thenReturn(null);

        String rawPassword = "12121212";
        String encodedPassword = "encoded-password-hash";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        changelog.execute();

        verify(passwordEncoder).encode(rawPassword);
    }
}
