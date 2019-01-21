package com.jhcomn.lambda.framework.lambda.base.common.strategy;

/**
 * Created by shimn on 2016/12/27.
 */
public class StrategyContext {

    protected IStrategy strategy;

    public IStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }
}
