package com.jhcomn.lambda.framework.lambda.base.common.observer;


import com.jhcomn.lambda.packages.IPackage;

/**
 * 抽象被观察主题角色
 * Created by shimn on 2016/12/26.
 */
public interface IObservable {

    void attach(IObserver observer);

    void detach(IObserver observer);

    void notify(IPackage pkg);

}
