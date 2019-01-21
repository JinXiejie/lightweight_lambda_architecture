package com.jhcomn.lambda.framework.lambda.base.common.observer;


import com.jhcomn.lambda.packages.IPackage;

/**
 * 抽象观察者
 * Created by shimn on 2016/12/26.
 */
public interface IObserver {

    void update(IPackage pkg);

}
