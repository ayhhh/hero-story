package org.tinygame.herostory;


import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandler;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 主消息处理器
 */
public class MainMsgProcessor {
    /**
     * 单例对象
     */
    private static final MainMsgProcessor _instance = new MainMsgProcessor();
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    /**
     * 单线程线程池
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Thread - MainMsgProcessor");
            return thread;
        }
    });
    private MainMsgProcessor(){

    }

    /**
     * 获取单例对象
     * @return
     */
    public static MainMsgProcessor getInstance(){
        return _instance;
    }


    /**
     * 处理消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    public void process(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(ctx == null || msg == null){
            return ;
        }
        LOGGER.info("收到客户端消息,msgClazz={}, msgBody={}",msg.getClass().getSimpleName(),msg);

        _es.submit(()->{
            try {
                CmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
                if (cmdHandler != null) {
                    cmdHandler.handle(ctx, cast(msg));
                }
            } catch (Exception exception){
                LOGGER.error(exception.getMessage(),exception);
            }
        });

    }

    private static <TCmd extends GeneratedMessageV3> TCmd cast(Object msg){
        if(msg == null){
            return null;
        } else {
            return (TCmd) msg;
        }
    }

    public void process(Runnable r){
        if(r == null){
            return;
        }
        _es.submit(r);
    }
}
