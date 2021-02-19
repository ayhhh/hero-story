package org.tinygame.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MySqlSessionFactory {
    /**
     * mybatis sql 会话工厂
     */
    private static SqlSessionFactory _sqlSessionFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    private MySqlSessionFactory(){

    }

    public static void init(){
        try {
            _sqlSessionFactory = new SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("MyBatisConfig.xml")
            );

            // 测试数据库连接
            SqlSession sqlSession = getSqlSession();
            sqlSession.getConnection().createStatement().execute("select -1");
            sqlSession.close();
            LOGGER.info("MySQL数据库连接测试成功");

        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }

    public static SqlSession getSqlSession(){
        if(_sqlSessionFactory == null){
            throw new RuntimeException("_sqlSessionFactory尚未初始化");
        }
        return _sqlSessionFactory.openSession(true);
    }
    public static void main(String[] args) {
        MySqlSessionFactory.init();
        SqlSession session = MySqlSessionFactory.getSqlSession();
        System.out.println(session);

    }


}
