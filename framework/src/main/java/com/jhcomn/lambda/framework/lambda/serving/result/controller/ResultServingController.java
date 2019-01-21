package com.jhcomn.lambda.framework.lambda.serving.result.controller;

import com.jhcomn.lambda.framework.lambda.serving.result.base.AbstractServingController;
import com.jhcomn.lambda.packages.IPackage;
import com.jhcomn.lambda.packages.result.SqlResultModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello World 结果层控制器
 * Created by shimn on 2017/5/10.
 */
public class ResultServingController extends AbstractServingController {

    public static final String TABLENAME = "result";

    @Override
    public void saveOrUpdate(IPackage model) {
        SqlResultModel data = (SqlResultModel) model;
        String id = data.getId();
        //step1:查
        String querySql = "select * from " + TABLENAME + " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            SqlResultModel ret = jdbcUtils.findSingleRefResult(querySql, params, SqlResultModel.class);
            //step2:对比，分情况
            String sql;
            if (ret != null) {
                //找到id相同的记录，则update
                sql = "update " + TABLENAME + " set clientId = ? , type = ? , datas = ? where id = ? ";
                params.clear();
                params.add(data.getClientId());
                params.add(data.getType());
                params.add(data.getDatas());
                params.add(data.getId());
            }
            else {
                //未有该id的记录，则insert
                sql = "insert into " + TABLENAME + " (id, clientId, type, datas) values (?, ?, ?, ?)";
                params.clear();
                params.add(data.getId());
                params.add(data.getClientId());
                params.add(data.getType());
                params.add(data.getDatas());
            }
            //step3:excute
            if (jdbcUtils.updateByPreparedStatement(sql, params)) {
                System.out.println("insert or update result 成功...");
            } else {
                System.out.println("insert or update result 失败...");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void delete(String id) {
        super.delete(TABLENAME, id);
    }

    @Override
    public IPackage load(String id) {
        SqlResultModel model = null;
        String querySql = "select * from " + TABLENAME + " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            model = jdbcUtils.findSingleRefResult(querySql, params, SqlResultModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public void send(IPackage model) {
        //TODO producer kafka
        if (producer != null) {
            producer.send(model);
        }
        else {
            System.out.println("ServingProducer is null");
        }
    }
}
