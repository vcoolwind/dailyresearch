package com.blackstone.dailyresearch.overload.suspicious;

/**
 * 有继承关系的方法重载，是很容易产生问题的。
 * 实际应用中，基于接口（父类）的编程经常声明父类，但实际运行中赋值子类。会造成调用结合和预期不符。
 * Method overloading is compile-time polymorphism.
 * Method overriding is runtime polymorphism.
 *
 * Java will choose the most specific match,
 * in your case a boolean will be automatically converted using auto-boxing boolean <-> Boolean.
 * If you use any other type like String the Object variant will be used.
 *
 * When a method is invoked (§15.12), the number of actual arguments (and any explicit type arguments)
 * and the compile-time types of the arguments are used,
 * at compile time, to determine the signature of the method that will be invoked (§15.12.2).
 * If the method that is to be invoked is an instance method, the actual method to be invoked will be determined
 * at run time, using dynamic method lookup (§15.12.4).
 *
 * @author 王彦锋
 * @date 2018/6/28 9:51
 *
 */
class OverLoadImpl {
    public void show(Parent parent) {
        System.out.println("run in foo(Parent)");
    }

    public void show(Child child) {
        System.out.println("run in foo(Child)");
    }
}

public class OverLoadTest {
    public static void main(String[] args) {
        OverLoadImpl ol = new OverLoadImpl();
        Parent p = new Parent();
        Child c = new Child();
        ol.show(p);
        ol.show(c);

        System.out.println("----------------------------------");
        for (int i = 1; i <10 ; i++) {
            Parent obj;
            if(i%2==0){
                // 这里打印的还是执行到父类，为什么？
                // 这里虽然实例是Child对象，但声明对象时是Parent，方法重载是编译时确定，所以在时间调用时，方法
                // 还是根据Parent找到了show(Parent parent)。
                obj =new Child();
                ol.show(obj);
            }else{
                obj =new Parent();
                ol.show(obj);
            }

        }
    }

}