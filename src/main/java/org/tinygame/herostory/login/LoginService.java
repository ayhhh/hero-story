package org.tinygame.herostory.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperation;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.login.db.UserDao;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;

import javax.security.auth.callback.Callback;
import java.util.function.Function;

/**
 * 登录服务
 */
public final class LoginService {

    /**
     * 单例对象
     */
    private static final LoginService _instance = new LoginService();

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private LoginService(){

    }

    /**
     * 获取单例对象
     * @return
     */
    public static LoginService getInstance(){
        return _instance;
    }

    public void userLogin(String userName, String password, Function<UserEntity,Void> callback){
        if(userName == null || password == null){
            return;
        }

        AsyncGetUserEntity asyncOp = new AsyncGetUserEntity(userName,password){
            @Override
            public void doFinish() {
                if(callback != null){
                    callback.apply(this.getUserEntity());
                }
            }

            @Override
            public int getBindId() {
                return userName.hashCode();
            }
        };
        AsyncOperationProcessor.getInstance().process(asyncOp);

    }

    private class AsyncGetUserEntity implements AsyncOperation{
        private final String userName;
        private final String password;
        private UserEntity _userEntity;

        public UserEntity getUserEntity() {
            return _userEntity;
        }

        public AsyncGetUserEntity(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        public void doAsync() {
            try(SqlSession sqlSession = MySqlSessionFactory.getSqlSession()) {
                // 获取DAO
                UserDao dao = sqlSession.getMapper(UserDao.class);
                // 查询
                UserEntity userEntity = dao.getByUserName(userName);
                // 判断密码是否相同
                if (userEntity != null) {
                    if (!password.equals(userEntity.getPassword())) {
                        throw new RuntimeException("密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.setUserName(userName);
                    userEntity.setPassword(password);
                    userEntity.setHeroAvatar("");

                    dao.insertUserEntity(userEntity);
                }

                // 登录时写入redis
                updateBasicInfoInRedis(userEntity);
                _userEntity = userEntity;

            } catch (Exception exception){
                LOGGER.error(exception.getMessage(),exception);
            }
        }
    }

    public void setHeroAvatar(Integer userId, String heroAvatar){
        if(userId == null || heroAvatar == null){
            return ;
        }
        try(SqlSession sqlSession = MySqlSessionFactory.getSqlSession()){
            // 获取DAO
            UserDao dao = sqlSession.getMapper(UserDao.class);
            // 插入
            dao.updateAvatarByUserId(userId,heroAvatar);

        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }

    }

    /**
     * 写入用户信息到redis
     * @param userEntity
     */
    private void updateBasicInfoInRedis(UserEntity userEntity){
        if(userEntity == null){
            return ;
        }
        try(Jedis redis = RedisUtil.getJedis()){
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userName",userEntity.getUserName());
            jsonObj.put("heroAvatar",userEntity.getHeroAvatar());
            redis.hset("User_"+ userEntity.getUserId(),"BasicInfo",jsonObj.toJSONString());

        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }
}
