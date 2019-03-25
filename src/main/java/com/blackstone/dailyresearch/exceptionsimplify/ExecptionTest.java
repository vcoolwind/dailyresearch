package com.blackstone.dailyresearch.exceptionsimplify;

public class ExecptionTest {

    public static void test1() throws MyException{
        System.out.println("1");
        throw new MyException();
    }

    public static void test2() throws Exception{
        System.out.println("2");
        throw new RuntimeException();
    }

   public static void main(String[] args){
       System.out.println("0");

       try{
           test1();
       }catch (Exception e){
           e.printStackTrace();
       }

       try{
            test2();
       }catch (Exception e){
           e.printStackTrace();
       }

    }
}
