package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainMsgProcessor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 异步操作处理类
 */
public class AsyncOperationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 单例对象
     */
    private static final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 线程池数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];


    /**
     * 私有化
     */
    private AsyncOperationProcessor(){
        for (int i = 0;i< _esArray.length;i++){
            final String threadName = "Thread - AsyncOperationProcessor - "+i;
            _esArray[i] = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName(threadName);
                    return thread;
                }
            });
        }

    }

    public static AsyncOperationProcessor getInstance(){
        return _instance;
    }


    /**
     * 执行异步操作
     * @param op
     */
    public void process(AsyncOperation op){
        if(op == null){
            return ;
        }
        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % _esArray.length;
        _esArray[esIndex].submit(()->{
            // 执行异步操作
            op.doAsync();
            // 回到主线程执行完成逻辑
            MainMsgProcessor.getInstance().process(op::doFinish);
        });
    }

}
