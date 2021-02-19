package org.tinygame.herostory.async;

public interface AsyncOperation {
    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish(){
    }

    /**
     * 获取绑定Id
     * @return
     */
    default int getBindId(){
        return 0;
    }

}
