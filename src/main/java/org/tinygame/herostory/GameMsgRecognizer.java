package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public final class GameMsgRecognizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);
    /**
     * 消息编号->消息对象
     */
    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();

    /**
     * 消息类->消息编号
     */
    private static final Map<Class<?>, Integer> _clazzAndMsgCodeMap = new HashMap<>();

    public static void init(){
        // 拿到所有的内部类
        Class<?>[] innerClasses = GameMsgProtocol.class.getDeclaredClasses();
        for(Class<?> innerClazz: innerClasses){
            // 如果不是消息类，则跳过
            if(innerClazz == null || !GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                continue;
            }

            // 获取类名称并小写
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            for(GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()){
                if(msgCode == null){
                    continue;
                }
                // 获取消息的名称
                String strMsgCodeName = msgCode.name();
                strMsgCodeName = strMsgCodeName.replaceAll("_","");
                strMsgCodeName = strMsgCodeName.toLowerCase();
                if(!strMsgCodeName.startsWith(clazzName)){
                    continue;
                }
                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    LOGGER.info("{} <--->{}",innerClazz.getName(),msgCode.getNumber());
                    _msgCodeAndMsgObjMap.put(msgCode.getNumber(), (GeneratedMessageV3) returnObj);

                    _clazzAndMsgCodeMap.put(innerClazz, msgCode.getNumber());
                } catch (Exception exception){
                    LOGGER.error(exception.getMessage(),exception);
                }


            }
        }
        /**  被上面的反射代码替代了
        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
        */

        /**
        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        _clazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
        */
    }
    private GameMsgRecognizer(){

    }

    public static Message.Builder getBuilderByMsgCode(int msgCode){
        if(msgCode < 0){
            return null;
        }
        GeneratedMessageV3 defaultMsg = _msgCodeAndMsgObjMap.get(msgCode);
        if(defaultMsg == null){
            return null;
        } else {
            return defaultMsg.newBuilderForType();
        }
    }

    public static int getMessageCodeByClazz(Class<?> msgClazz){
        Integer msgCode =  _clazzAndMsgCodeMap.get(msgClazz);
        if(msgCode == null){
            return -1;
        } else {
            return msgCode;
        }
    }
}
