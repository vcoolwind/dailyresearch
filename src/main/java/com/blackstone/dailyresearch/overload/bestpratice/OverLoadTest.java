package com.blackstone.dailyresearch.overload.bestpratice;

/**
 * 有继承关系的方法重载，是很容易产生问题的。
 * 实际应用中，基于接口（父类）的编程经常声明父类，但实际运行中赋值子类。会造成调用结合和预期不符。
 * Method overloading is compile-time polymorphism.
 * Method overriding is runtime polymorphism.
 * <p>
 * Java will choose the most specific match,
 * in your case a boolean will be automatically converted using auto-boxing boolean <-> Boolean.
 * If you use any other type like String the Object variant will be used.
 * <p>
 * When a method is invoked (§15.12), the number of actual arguments (and any explicit type arguments)
 * and the compile-time types of the arguments are used,
 * at compile time, to determine the signature of the method that will be invoked (§15.12.2).
 * If the method that is to be invoked is an instance method, the actual method to be invoked will be determined
 * at run time, using dynamic method lookup (§15.12.4).
 *
 * @author 王彦锋
 * @date 2018/6/28 9:51
 */
class OverLoadImpl {

    public boolean show(Parent p) {
        if (p instanceof ChildP) {
           return show((ChildP) p);
        } else if (p instanceof ChildA) {
           return show((ChildA) p);
        }
        throw new UnsupportedOperationException(
                String.format("this method can't support the class [%s], do you forget add it?", p.getClass().getName())
        );
    }

    public boolean show(ChildP childP) {
        System.out.println("run in foo(childP)");
        return  true;
    }

    public boolean show(ChildA childA) {
        System.out.println("run in foo(ChildA)");
        return  true;
    }

    public boolean show(ChildB childB) {
        System.out.println("run in foo(ChildB)");
        return  true;
    }


}

public class OverLoadTest {
    public static void main(String[] args) {
        OverLoadImpl ol = new OverLoadImpl();
        ChildP p = new ChildP();
        ChildA a = new ChildA();
        ChildB b = new ChildB();
        ol.show(p);
        ol.show(a);
        ol.show(b);
        System.out.println("-------------");
        Parent p1 = new ChildP();
        Parent a1 = new ChildA();
        Parent b1 = new ChildB();
        ol.show(p1);
        ol.show(a1);
        ol.show(b1);


    }

}