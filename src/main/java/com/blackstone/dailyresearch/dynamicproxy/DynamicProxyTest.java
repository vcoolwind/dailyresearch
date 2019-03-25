package com.blackstone.dailyresearch.dynamicproxy;

public class DynamicProxyTest {
    public static void main(String[] args){
        IUserDao target=new UserDao();
        System.out.println(target.getClass());

        IUserDao proxy=(IUserDao) new ProxyFactory(target).getProxyInstance();
        System.out.println(proxy.getClass());
        proxy.getUserNames();

        TradeService service =  new TradeService();
        System.out.println(service.getClass());

        TradeService proxyService=(TradeService) new ProxyFactory(service).getProxyInstance();
        System.out.println(proxy.getClass());
        System.out.println(proxyService.add(1,2));


        //final類不能被代理
        FinalService finalService=  new FinalService();
        System.out.println(finalService.getClass());
        FinalService proxyService2=(FinalService) new ProxyFactory(service).getProxyInstance();
        System.out.println(proxy.getClass());
        proxyService2.sayHello("aaa");
    }
}
