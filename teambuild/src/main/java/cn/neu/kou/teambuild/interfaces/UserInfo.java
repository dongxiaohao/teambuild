package cn.neu.kou.teambuild.interfaces;

import cn.neu.kou.teambuild.interfaces.Link;

import java.util.HashMap;

/**
 * 用户信息封装
 * @author : lq
 * @date : 2019/3/29
 */
public class UserInfo {
    private int id;       //用户的id编号
    private int label;    //用户的标签
    private HashMap<Integer, Link> adjLinkMap; //为了快速查找查询图中某个用户节点邻居和权重定义数据结构数据结构，map形式为{用户id:{邻居用户id:到邻居用户的Link}}

    /**
     * 实例化用户信息
     * @param id     当前用户的id
     * @param label  当前用户的label
     */
    public UserInfo(int id, int label) {
        this.id = id;
        this.label = label;
        this.adjLinkMap = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public HashMap<Integer, Link> getAdjLinkMap() {
        return adjLinkMap;
    }

    public void setAdjLinkMap(HashMap<Integer, Link> adjLinkMap) {
        this.adjLinkMap = adjLinkMap;
    }
}
