package cn.neu.kou.teambuild.interfaces;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public interface GraphInterface {
    /**
     * 找到当前节点的所有直接邻居
     * @param userId    当前用户的id
     * @return          返回当前用户的直接邻居
     */

    List<Link> findNeighbors(int userId);

    /**
     * 两个节点之间的权重（中间无其他的节点，不可达返回Float.MAX_VALUE）
     * @param fromUserId   源用户节点id
     * @param toUserId     目的用户节点id
     * @return             两个用户之间权重
     */
    float getDirectWeight(int fromUserId, int toUserId);

    /**
     * 返回用户id为userId的用户的标签值
     * @param userId     用户id
     * @return           用户的标签
     */
    int getLabel(int userId);

    /**
     * 将邻接表转化为邻接矩阵
     * @return   邻接矩阵
     */
    int[][] toAdjMatrix();

    /**
     * 得到标签列表
     * @return
     */
    List<Integer> getLabelsArr();

    /**
     * 获取图中有多少节点
     * @return
     */
    int getNodeNumber();
}
