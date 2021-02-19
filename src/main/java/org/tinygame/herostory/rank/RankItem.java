package org.tinygame.herostory.rank;

/**
 * 排名条目
 */
public class RankItem {
    private int rankId;
    private int userId;
    private  String userName;
    private String heroAvatar;
    private int win;

    @Override
    public String toString() {
        return "RankItem{" +
                "rankId=" + rankId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", heroAvatar='" + heroAvatar + '\'' +
                ", win=" + win +
                '}';
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
}
