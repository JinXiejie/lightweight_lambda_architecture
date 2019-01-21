package com.jhcomn.lambda.packages;

/**
 * 数据包类型枚举
 * Created by shimn on 2016/12/27.
 */
public enum PackageType {

    TEST(0, "TEST"),

    UW(1, "UW"),

    TEV(2, "TEV"),

    INFRARED(3, "INFRARED"),

    HFCT(4, "HFCT"),

    HELLO_WORLD(5, "HELLO_WORLD"),

    UHF(6, "UHF");

    private int value;

    private String description;

    private PackageType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int value() {
        return value;
    }

    public String description() {
        return description;
    }

    public static PackageType valueOfKey(int value) {
        for (PackageType type : PackageType.values()) {
            if (type.value() == value)
                return type;
        }
        return null;
    }

    public static PackageType valueOfDesc(String description) {
        for (PackageType type : PackageType.values()) {
            if (type.description.trim().equalsIgnoreCase(description.trim())) {
                return type;
            }
        }
        return null;
    }
}
