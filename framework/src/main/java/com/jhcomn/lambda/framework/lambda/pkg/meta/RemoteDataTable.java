package com.jhcomn.lambda.framework.lambda.pkg.meta;

/**
 * HBase存储kafka原始数据源表结构
 * 训练集数据
 * row key = userId_time_uuid
 * Created by shimn on 2016/12/23.
 */
public class RemoteDataTable {

    public static final String tableName = "remote_data";

    public static final String[] columnFamilys = new String[] {"info", "list"};

    public static final String infoFamily = "info";

    public static final String idColumn = "id";

    public static final String clientIdColumn = "clientId";

    public static final String typeColumn = "type";

    public static final String urlColumn = "url";

    public static final String listFamily = "list";

    public static final String dataColumn = "data";

//    public static final String tagColumn = "tag";
}
