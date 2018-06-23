package com.balckstone.dailyresearch.designpatterns.template.completionservice.v3;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * desc: CompletionService模板回调类
 * @param <V> 任务执行返回值
 * @author 王彦锋
 * @date 2018/6/22 20:50
 */
public interface CompletionServiceCallback<V> {

    /**
     * 生产要执行的任务列表
     * @return
     * @author 王彦锋
     * @date 2018/6/22 20:51
     *
     */
    List<Callable<V>> genCallables();

    /**
     * 处理每个任务执行产生的结果
     * @param result
     */
    void handleResult(V result);
}
