package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 命令处理器工厂类
 */
public final class CmdHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private static Map<Class<?>, CmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory(){

    }

    public static void init(){
        /**
         * 利用反射重构
         */
        LOGGER.info("命令与处理器正在关联...");
        // 获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        // 获取CmdHandler所有实现类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, CmdHandler.class);

        for(Class<?> handlerClazz: clazzSet){
            if(handlerClazz == null || (handlerClazz.getModifiers() & Modifier.ABSTRACT)!=0){
                // 如果是抽象类
                continue;
            }
            Method[] declaredMethods = handlerClazz.getDeclaredMethods();
            Class<?> cmdClazz = null;
            for(Method currMethod: declaredMethods){
                if(currMethod == null || !currMethod.getName().equals("handle")){
                    continue;
                }
                // 获取的函数参数类型数组
                Class<?>[] parameterTypes = currMethod.getParameterTypes();
                if(parameterTypes.length < 2 ||
                        parameterTypes[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])){
                    continue;
                }

                // 拿到想要的类型了
                cmdClazz = parameterTypes[1];
                break;
            }

            if(cmdClazz == null){
                continue;
            }

            try {
                // 创建命令处理器实例
                CmdHandler<?> newHandler = (CmdHandler<?>) handlerClazz.newInstance();
                LOGGER.info("{} <---> {}",cmdClazz.getName(),handlerClazz.getName());
                _handlerMap.put(cmdClazz,newHandler);
            } catch (Exception exception){
                LOGGER.error(exception.getMessage(),exception);
            }


        }
        /**
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
         */
    }

    /**
     * 创建命令处理器
     * @param msgClazz
     * @return
     */
    public static CmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz){
        if(msgClazz == null){
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
