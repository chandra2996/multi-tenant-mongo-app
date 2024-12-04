package com.multitenant.demo.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    private String id;
    private String username;

    private String firstName;

    private String lastName;
}
