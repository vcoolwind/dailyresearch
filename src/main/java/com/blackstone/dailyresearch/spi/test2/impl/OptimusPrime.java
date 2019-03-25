package com.blackstone.dailyresearch.spi.test2.impl;


import com.blackstone.dailyresearch.spi.test2.Robot;

public class OptimusPrime implements Robot {

    @Override
    public void sayHello() {
        System.out.println("Hello, I am Optimus Prime.");
    }
}