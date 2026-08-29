package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceRequest {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String type;
    private Boolean available;
}
