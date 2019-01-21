package com.jhcomn.lambda.framework.lambda.pkg.meta;

/**
 * 标签清单表
 * rowKey = type_uuid
 * Created by shimn on 2017/5/18.
 */
public class TagListDataTable {

    public static final String tableName = "tag_list";

    public static final String[] columnFamilys = new String[] {"info"};

    public static final String infoFamily = "info";

    public static final String idColumn = "id";

    public static final String contentColumn = "content";

}
