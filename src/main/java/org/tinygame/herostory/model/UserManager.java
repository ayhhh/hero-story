package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理器
 */
public final class UserManager {
    /**
     * 用户字典
     */
    private static final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    /**
     * 私有化构造器
     */
    private UserManager(){

    }

    /**
     * 添加用户
     * @param user
     */
    public static void addUser(User user){
        if(user != null) {
            _userMap.putIfAbsent(user.getUserId(), user);
        }
    }

    /**
     * 移除用户
     * @param userId
     */
    public static void removeByUserId(int userId){
        _userMap.remove(userId);
    }

    public static Collection<User> listUser(){
        return _userMap.values();
    }

    public static User getByUserId(int userId){
        return _userMap.get(userId);
    }
}
