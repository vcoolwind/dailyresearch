package com.blackstone.dailyresearch.designpatterns.template.completionservice.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * desc: CompletionService模板回调类
 *
 * @param <V> 任务执行返回值
 * @author 王彦锋
 * @date 2018/6/22 20:50
 */
public abstract class CompletionServiceCallback<V> {
    private List<Callable<V>> callList = new ArrayList<>();

    /**
     * 生产要执行的任务列表
     *
     * @return
     * @author 王彦锋
     * @date 2018/6/22 20:51
     */
    public void addCallable(Callable<V> callable) {
        callList.add(callable);
    }

    public List<Callable<V>> getCallables() {
        handleTask();
        if(callList.size()==0){
            throw new IllegalArgumentException("No tasks to perform.Do you invoke addCallable() in handleTask?");
        }
        return callList;
    }

    abstract  void handleTask();
    /**
     * 处理每个任务执行产生的结果
     *
     * @param result
     */
    abstract void handleResult(V result);
}
