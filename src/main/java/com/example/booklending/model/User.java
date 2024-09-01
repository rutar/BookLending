package com.example.booklending.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Setter
@Getter
@ApiModel(description = "Represents a user in the system with associated credentials and role.")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The unique ID of the user", example = "1", required = true, position = 1)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    @ApiModelProperty(notes = "The username of the user", example = "johndoe", required = true, position = 2)
    private String username;

    @Column(name = "password", nullable = false)
    @ApiModelProperty(notes = "The password of the user", example = "password123", required = true, position = 3)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    @ApiModelProperty(notes = "The email address of the user", example = "johndoe@example.com", required = true, position = 4)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    @ApiModelProperty(notes = "The role assigned to the user", required = true, position = 5)
    private Role role;
}
