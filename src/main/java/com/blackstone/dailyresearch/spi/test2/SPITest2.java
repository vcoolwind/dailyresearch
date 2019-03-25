package com.blackstone.dailyresearch.spi.test2;

import com.blackstone.dailyresearch.spi.test2.Robot;
import org.junit.Test;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * desc:  定义一个接口，实现者继承这个接口即可。
 * 在resources目录添加 META-INF/services 目录，使用接口类，注入实现类。
 *
 * @author 王彦锋
 * @date 2019/3/23 10:08
 */
public class SPITest2 {

    @Test
    public void testSpi() {
        ServiceLoader<Robot> serviceLoader = ServiceLoader.load(Robot.class);
        System.out.println("Java SPI...");

        Iterator<Robot> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            iterator.next().sayHello();
        }
    }

    public static void main(String[] args) {
        ServiceLoader<Robot> serviceLoader = ServiceLoader.load(Robot.class);
        System.out.println("Java SPI...");

        Iterator<Robot> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            iterator.next().sayHello();
        }
    }


}
