package com.zhvavyy.backend.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Status  implements GrantedAuthority {
    DONE,PENDING,ACTIVE;

    @Override
    public String getAuthority() {
        return name();
    }
}