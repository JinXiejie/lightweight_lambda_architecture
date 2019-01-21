package com.jhcomn.lambda.framework.lambda.persistence.hbase;

import com.jhcomn.lambda.framework.lambda.base.common.constants.ConstantDatas;
import com.jhcomn.lambda.framework.lambda.persistence.client.AbstractDaoClient;
import com.jhcomn.lambda.framework.lambda.persistence.client.IDaoClient;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseRowDao;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.dao.HBaseTableDao;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.impl.HBaseRowDaoImpl;
import com.jhcomn.lambda.framework.lambda.persistence.hbase.impl.HBaseTableDaoImpl;
import com.jhcomn.lambda.framework.lambda.pkg.meta.UserRecommendDataTable;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.PackageType;
import com.jhcomn.lambda.packages.test.UserMLResultPackage;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * HBase DAO 客户端
 * Created by shimn on 2017/1/16.
 */
public class HBaseDaoClient extends AbstractDaoClient {

    private HBaseTableDao tableDao;
    private HBaseRowDao rowDao;

    @Override
    public void init() {
        if (tableDao == null) {
            tableDao = new HBaseTableDaoImpl();
            tableDao.init();
        }
        if (rowDao == null) {
            rowDao = new HBaseRowDaoImpl();
            rowDao.init();
        }
    }

    @Override
    public void save(IPackage pkg) {
        super.save(pkg);
        if (this.pkgType == PackageType.TEST) {
            System.out.println("saveOrUpdate ML result into HBase now.");
            try {
                tableDao.createTable(UserRecommendDataTable.tableName, UserRecommendDataTable.columnFamilys);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserMLResultPackage userResult = (UserMLResultPackage) pkg;
            Set<Long> sets = userResult.getResults().keySet();
            List<Put> puts = new ArrayList<>();
            long index = 0L;
            for (Long key : sets) {
                puts.add(getPut(
                            key + "",
                            UserRecommendDataTable.resultFamily,
                            UserRecommendDataTable.uidColumn,
                            userResult.getResults().get(key)
                        ));
                if ((++index) % ConstantDatas.HBASE_FLUSH_SIZE == 0) {
                    try {
                        rowDao.insert(UserRecommendDataTable.tableName, puts);
                        puts.clear();
                        System.out.println("insert into HBase successfully now. The time is " + index);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //insert剩下的记录
            if (puts.size() > 0) {
                try {
                    rowDao.insert(UserRecommendDataTable.tableName, puts);
                    puts.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("saveOrUpdate into HBase finished now.");
    }

    @Override
    public void delete(IPackage pkg) {

    }

    @Override
    public void close() {
        if (tableDao != null)
            tableDao.close();
        if (rowDao != null)
            rowDao.close();
    }

    /**
     * 获取HBase批量插入put
     * @param key
     * @param family
     * @param column
     * @param value
     * @return
     */
    private Put getPut(String key, String family, String column, String value) {
        Put put = new Put(Bytes.toBytes(key));
        put.addColumn(Bytes.toBytes(family),
                Bytes.toBytes(column),
                Bytes.toBytes(value));
        return put;
    }
}
