package com.blackstone.dailyresearch.dynamicproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ProxyFactory   {
    private Object target;
    public ProxyFactory(Object target){
        this.target=target;
    }
    public Object getProxyInstance(){
        Object[] interfaces = target.getClass().getInterfaces();
        if(interfaces!=null&&interfaces.length>0){
            // JDKProxy 面向接口動態增強

            //沒有繼承接口的類無法使用jdk代理
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            System.out.println("start --- method = [" + method + "], args = [" + args + "]");
                            Object result = method.invoke(target,args);
                            System.out.println("end ---  method = [" + method + "], args = [" + args + "]");
                            return result;
                        }
                    }
            );
        }else{
            //面向子類動態增強
            //子類實現，final類無法代理。
            Enhancer en = new Enhancer();
            en.setSuperclass(target.getClass());
            en.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    System.out.println("start --- method = [" + method + "], args = [" + objects + "]");
                    Object result = method.invoke(target,objects);
                    System.out.println("end --- method = [" + method + "], args = [" + objects + "]");
                    return result;                }
            });
            return en.create();
        }


    }

}

