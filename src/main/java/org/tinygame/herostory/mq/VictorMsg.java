package org.tinygame.herostory.mq;

/**
 * 胜利消息
 */
public class VictorMsg {

    private int winnerId;
    private int loserId;

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public int getLoserId() {
        return loserId;
    }

    public void setLoserId(int loserId) {
        this.loserId = loserId;
    }
}
