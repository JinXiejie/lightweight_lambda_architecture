package com.jhcomn.lambda.framework.lambda.sync.model;

import com.jhcomn.lambda.framework.lambda.sync.base.AbstractSyncController;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.ml_model.SyncMLModel;

import java.util.ArrayList;
import java.util.List;

/**
 * hello-world同步模型控制器
 * Created by shimn on 2017/4/24.
 */
public class ModelSyncController extends AbstractSyncController {

    public static final String TABLENAME = "model";

    @Override
    public void saveOrUpdate(IPackage model) {
        SyncMLModel data = (SyncMLModel) model;
        String id = data.id;
        //step1:查
        String querySql = "select * from " + TABLENAME + " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            SyncMLModel ret = jdbcUtils.findSingleRefResult(querySql, params, SyncMLModel.class);
            //step2：对比，分情况
            String sql;
            if (ret != null) {
                //找到id相同的记录，则update
                sql = "update " + TABLENAME + " set clientId = ? , model = ? , time = ? , type = ? where id = ? ";
                params.clear();
                params.add(data.clientId);
                params.add(data.model);
                params.add(data.time);
                params.add(data.type);
                params.add(data.id);
            }
            else {
                //未有该id的记录，则insert
                sql = "insert into " + TABLENAME + " (id, clientId, model, time, type) values (?, ?, ?, ?, ?)";
                params.clear();
                params.add(data.id);
                params.add(data.clientId);
                params.add(data.model);
                params.add(data.time);
                params.add(data.type);
            }
            //step3：execute
            if (jdbcUtils.updateByPreparedStatement(sql, params)) {
                System.out.println("insert or update model 成功...");
            } else {
                System.out.println("insert or update model 失败...");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
//        String sql = "if exists (select id from model where id = " + data.id + ")" +
//                "   begin" +
//                "   update model set clientId = " + data.clientId +
//                ", type = " + data.type +
//                ", time = " + data.time +
//                ", model = " + data.model +
//                "   where id = " + data.id +
//                "   end" +
//                "else" +
//                "   begin" +
//                "   insert into model (id, clientId, type, time, model) values (" +
//                data.id + "," + data.clientId + "," + data.type + "," + data.time + "," + data.model + ")" +
//                "   end";
    }

    @Override
    public void delete(String id) {
        super.delete(TABLENAME, id);
    }


    @Override
    public IPackage load(String id) {
        SyncMLModel model = null;
        String querySql = "select * from " + TABLENAME + " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            model = jdbcUtils.findSingleRefResult(querySql, params, SyncMLModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

}
