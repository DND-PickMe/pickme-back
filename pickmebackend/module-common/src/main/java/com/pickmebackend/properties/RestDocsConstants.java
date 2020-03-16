package com.pickmebackend.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum RestDocsConstants {
    PROFILE("profile"),
    UPDATE_ACCOUNT("update-account"),
    DELETE_ACCOUNT("delete-account"),
    LOGIN_ACCOUNT("login-account"),
    UPDATE_ENTERPRISE("update-enterprise"),
    DELETE_ENTERPRISE("delete-enterprise"),
    LOGIN_ENTERPRISE("login-enterprise"),
    DELETE_EXPERIENCE("delete-experience"),
    UPDATE_EXPERIENCE("update-experience"),
    CREATE_EXPERIENCE("create-experience"),
    UPDATE_LICENSE("update-license"),
    DELETE_LICENSE("delete-license"),
    CREATE_LICENSE("create-license"),
    CREATE_PRIZE("create-prize"),
    CREATE_PROJECT("create-project"),
    CREATE_SELF_INTERVIEW("create-selfInterview"),
    LOAD_ALL_ENTERPRISE("load-allEnterprises"),
    LOAD_ALL_ACCOUNT("load-allAccounts"),
    UPDATE_PRIZE("update-prize"),
    DELETE_PRIZE("delete-prize"),
    UPDATE_PROJECT("update-project"),
    DELETE_PROJECT("delete-project"),
    UPDATE_SELF_INTERVIEW("update-selfInterview"),
    DELETE_SELF_INTERVIEW("delete-selfInterview");

    private String value;
}
