package com.jhcomn.lambda.framework.lambda.base.common.observer;

import com.jhcomn.lambda.packages.IPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shimn on 2017/1/18.
 */
public class BaseUpdateObservable implements IObservable {

    private List<IObserver> lists = new ArrayList<>();

    @Override
    public void attach(IObserver observer) {
        lists.add(observer);
    }

    @Override
    public void detach(IObserver observer) {
        lists.remove(observer);
    }

    @Override
    public void notify(IPackage pkg) {
        for (IObserver observer : lists) {
            observer.update(pkg);
        }
    }

}
