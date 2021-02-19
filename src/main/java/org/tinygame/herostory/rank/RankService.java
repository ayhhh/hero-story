package org.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.common.filter.impl.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperation;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.util.PackageUtil;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public class RankService {
    /**
     * 单例对象
     */
    private static final RankService _instance = new RankService();

    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    private RankService(){

    }

    /**
     * 获取单例对象
     * @return
     */
    public static RankService getInstance(){
        return _instance;
    }

    /**
     * 获取排名
     * 这也是一个IO，所以最好也是异步的方式
     * 所以这里也使用回调
     */
    public void getRank(Function<List<RankItem>,Void> callback){
        AsyncOperation op = new AsyncGetRank(){
            @Override
            public void doFinish() {
                if(callback == null){
                    return;
                }
                callback.apply(this.getItemList());
            }
        };
        AsyncOperationProcessor.getInstance().process(op);
    }

    public void refreshRank(int winnerId, int loserId){
        if(winnerId <=0 || loserId <= 0){
            return ;
        }
        try(final Jedis redis = RedisUtil.getJedis()){
            redis.hincrBy("User_" + winnerId, "Win",1);
            redis.hincrBy("User_" + loserId, "Lose",1);

            final String win = redis.hget("User_" + winnerId, "Win");
            int winNum = Integer.parseInt(win);

            redis.zadd("Rank",winNum,String.valueOf(winnerId));

        } catch (Exception exception){
            LOGGER.error(exception.getMessage(),exception);
        }
    }

    /**
     * 异步方式拿到排名
     */
    private class AsyncGetRank implements AsyncOperation{
        private List<RankItem> _itemList;
        @Override
        public void doAsync() {
            // 去redis拿数据
            try(Jedis redis = RedisUtil.getJedis()){
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank",0,9); // z-set
                int i = 0;
                List<RankItem> rankItemList = new ArrayList<>();
                for(Tuple t: valSet){
                    if(t == null){
                        continue;
                    }
                    // 获取用户Id
                    Integer userId = Integer.parseInt(t.getElement());
                    // 获取用户信息，保存在hash里面，是一个json字符串
                    String basicInfo = redis.hget("User_" + userId, "BasicInfo");

                    if(basicInfo == null){
                        continue;
                    }

                    JSONObject jsonObj = JSONObject.parseObject(basicInfo);

                    RankItem rankItem = new RankItem();
                    rankItem.setRankId(++i);
                    rankItem.setUserId(userId);
                    rankItem.setWin((int) t.getScore());
                    rankItem.setUserName(jsonObj.getString("userName"));
                    rankItem.setHeroAvatar(jsonObj.getString("heroAvatar"));

                    rankItemList.add(rankItem);

                }
                _itemList  = rankItemList;

            } catch (Exception exception){
                LOGGER.error(exception.getMessage(),exception);
            }

        }

        /**
         * 获取排名条目列表
         * @return
         */
        public List<RankItem> getItemList() {
            return _itemList;
        }
    }
}
