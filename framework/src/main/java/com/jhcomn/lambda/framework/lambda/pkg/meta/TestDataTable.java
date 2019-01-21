package com.jhcomn.lambda.framework.lambda.pkg.meta;

/**
 * HBase存储kafka原始数据源表结构
 * 测试集数据
 * Created by shimn on 2017/5/5.
 */
public class TestDataTable {

    public static final String tableName = "test_data";

    public static final String[] columnFamilys = new String[]{"info", "list"};

    public static final String infoFamily = "info";

    public static final String idColumn = "id";

    public static final String clientIdColumn = "clientId";

    public static final String typeColumn = "type";

    public static final String urlColumn = "url";

    public static final String listFamily = "list";

    public static final String dataColumn = "data";
}
