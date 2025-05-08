package com.boot.web.security.util;

import com.boot.web.domain.AdminUserDetails;
import com.boot.web.modules.ums.model.UmsAdmin;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     */
    public UmsAdmin getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AdminUserDetails) {
            AdminUserDetails adminUserDetails =  (AdminUserDetails) principal;
            return adminUserDetails.getUmsAdmin();
        }
        return null;
    }

    /**
     * 获取用户
     */
    public UmsAdmin getUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return getUser(authentication);
    }

    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId() {
        UmsAdmin admin = getUser();
        return admin != null ? admin.getId() : null;
    }
}