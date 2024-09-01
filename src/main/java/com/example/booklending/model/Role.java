package com.example.booklending.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
@Setter
@Getter
@ApiModel(description = "Represents a user role in the system.")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The unique ID of the role", example = "1", required = true, position = 1)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    @ApiModelProperty(notes = "The name of the role", example = "ADMIN", required = true, position = 2)
    private String name;

    // Custom constructor should not affect Swagger documentation
    public Role(String name) {
        this.name = name;
    }
}
