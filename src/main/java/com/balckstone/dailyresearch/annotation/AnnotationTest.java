package com.balckstone.dailyresearch.annotation;


import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/6/21 18:04
 */
public class AnnotationTest {


    public static void main(String[] args) {
        AnnotationTest test = new AnnotationTest();
        System.out.println(test.getStr("abc"));
        System.out.println(test.getStr(null));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        System.out.println(validator.validate(test));        ;
    }

    @NotNull public String getStr(@NotNull String ins) {
        return null;
    }
}
