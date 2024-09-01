package com.example.booklending.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "A generic class representing a paginated response, which contains a list of items and pagination details.")
public class PagedResponse<T> {

    @ApiModelProperty(notes = "The list of items in the current page", position = 1)
    private List<T> content;

    @ApiModelProperty(notes = "The total number of elements available across all pages", example = "100", position = 2)
    private long totalElements;

    @ApiModelProperty(notes = "The total number of pages available", example = "10", position = 3)
    private int totalPages;

    @ApiModelProperty(notes = "Indicates whether this is the last page", example = "false", position = 4)
    private boolean last;

    @ApiModelProperty(notes = "The number of items per page", example = "10", position = 5)
    private int size;

    @ApiModelProperty(notes = "The current page number", example = "1", position = 6)
    private int number;
}
