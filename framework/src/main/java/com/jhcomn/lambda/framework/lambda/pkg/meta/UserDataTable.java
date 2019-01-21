package com.jhcomn.lambda.framework.lambda.pkg.meta;

/**
 * 推荐用户User Table
 * Created by shimn on 2016/12/28.
 */
public class UserDataTable {

    public static final String tableName = "users";

    public static final String[] columnFamilys = new String[] {"info"};

    public static final String infoFamily = "info";

    public static final String idColumn = "id";

    public static final String accountColumn = "account";

    public static final String lastTimeColumn = "lastTime";

    public static final String genderColumn = "gender";

    public static final String ageColumn = "age";

    public static final String locationColumn = "location";

    public static final String provinceColumn = "province";

    public static final String cityColumn = "city";

    public static final String advantagedSubjectColumn = "advantagedSubject";

    public static final String disAdvantagedSubjectColumn = "disAdvantagedSubject";
}
