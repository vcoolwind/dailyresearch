package com.blackstone.dailyresearch.dynamicproxy;

public class UserDao implements IUserDao {
    @Override
    public String getUserNames() {
        return "张三;李四;";
    }
}
