package com.example.booklending.model;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Represents the status of a book in the library system.")
public enum BookStatus {
    AVAILABLE,
    RESERVED,
    LENT_OUT,
    BORROWED,
    RETURNED
}
