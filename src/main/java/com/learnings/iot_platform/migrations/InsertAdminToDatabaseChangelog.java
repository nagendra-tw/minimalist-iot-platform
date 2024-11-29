package com.learnings.iot_platform.migrations;

import com.learnings.iot_platform.model.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@ChangeUnit(id = "DB-admin-init", order = "1", author = "Nagendra")
public class InsertAdminToDatabaseChangelog {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    public InsertAdminToDatabaseChangelog(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Execution
    public void execute() {
        if (mongoTemplate.findOne(
                Query.query(Criteria.where("username").is("admin1")),
                User.class
        ) == null) {
            User user = new User();
            user.setUsername("admin1");
            user.setPasswordHash(passwordEncoder.encode("12121212"));
            user.setEmail("admin1@gmail.com");
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_ADMIN");
            user.setRoles(roles);

            mongoTemplate.insert(user);
        }
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.remove(
                Query.query(Criteria.where("username").is("admin1")),
                User.class
        );
    }
}
