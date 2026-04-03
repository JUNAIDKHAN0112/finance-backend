package com.zorvyn.finance.dto.request;

import com.zorvyn.finance.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}