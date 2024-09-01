package com.example.booklending.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Request object for user authentication.")
public class AuthRequest {

    @ApiModelProperty(notes = "The username of the user requesting authentication", example = "johndoe", required = true, position = 1)
    private String username;

    @ApiModelProperty(notes = "The password of the user requesting authentication", example = "password123", required = true, position = 2)
    private String password;
}
