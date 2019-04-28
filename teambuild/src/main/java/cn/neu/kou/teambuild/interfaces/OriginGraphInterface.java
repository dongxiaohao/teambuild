package cn.neu.kou.teambuild.interfaces;

import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public interface OriginGraphInterface extends GraphInterface {

    /**
     * 返回在满足约束的情况下的可达邻居（在权重和内能够到达）
     * @param userId   当前的中心用户节点
     * @param weight   约束条件（权重限制）
     * @return         所有满足约束条件的可达邻居集
     */
//<<<<<<< HEAD
	//ArrayList<Link> getAvailableNeighbors(int userId, float weight);
//=======
	List<Link> getAvailableNeighbors(int userId, float weight);

    /**
     * 返回在满足约束的情况下的指定标签类型的可达邻居（在权重和内能够到达）
     * @param userId   当前的中心用户节点
     * @param weight   约束条件（权重限制）
     * @param label    标签类型
     * @return         所有满足约束条件的可达邻居集
     */
    List<Link> getAvailableNeighbors(int userId, float weight, int label);
//>>>>>>> 93512cb74dec9fadd4de5f0f47c660ea022aac48

    /**
     * 返回所有的用户标签为label的用户节点
     * @param label    用户标签
     * @return         标签为label的用户下标数组
     */
    
    List<Integer> getUserWithLabel(int label);

    /**
     * 得到两个用户节点之间的最短距离
     * @param fromUserId    源用户id
     * @param toUserId      目的用户id
     * @param maxDistance   最大的距离（权重约束，或者说距离约束，避免不必要的搜索）
     * @return              两个用户之间的距离
     */
    float getMinDistance(int fromUserId, int toUserId, float maxDistance);
    /**
     * 添加一条边，若存在则返回“边已存在”
     * @param node1	第一个节点
     * @param node2  第二个节点
     * @param weight 添加权重
     */
    int addEdge(int node1,int node2,int weight);
    /**
     * 修改链接上的权重,链接不存在则返回“边不存在”
     * @param node1	第一个节点
     * @param node2 第二个节点
     * @param targat_weight 目标权重
     */
    void setWeight(int node1,int node2,int targat_weight);
    /**
     * 删除节点标签，实现即将对应节点标签设为整数最大值
     * @param userId 输入需要删除的节点序列
     */
    void del_label(List<Integer> userId);
    
    float getMinDistance(int fromUserId, int toUserId) ;
}
