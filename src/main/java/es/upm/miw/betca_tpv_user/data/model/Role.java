package es.upm.miw.betca_tpv_user.data.model;

public enum Role {
    ADMIN, MANAGER, OPERATOR, CUSTOMER, AUTHENTICATED;

    public static String prefix = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.prefix, ""));
    }

    public String withPrefix() {
        return prefix + this.toString();
    }

}
