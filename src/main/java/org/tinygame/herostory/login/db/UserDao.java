package org.tinygame.herostory.login.db;

public interface UserDao {
    public UserEntity getByUserName(String userName);
    public void insertUserEntity(UserEntity userEntity);
    public void updateAvatarByUserId(Integer userId, String heroAvatar);
}
