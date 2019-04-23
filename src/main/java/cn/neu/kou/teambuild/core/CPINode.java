package cn.neu.kou.teambuild.core;

import cn.neu.kou.teambuild.util.BloomFilter;

import java.util.HashMap;

/**
 * CPI路径上的一个节点
 * @author LQ
 * @date 2019-3-29
 */
public class CPINode {
    private int userId;   // 处理候选用户的id
    private int weight;   //路径到当前用户节点经过的路径的权重
    private HashMap<Integer, CPITreeNode> nextLabelCandidateCPITreeNodeMap;   //下一类型标签的候选者集合
    private BloomFilter usedBloomFilter;                                //bloom过滤器，用于记录之前已经访问的用户id

    /**
     * CPI路径上节点的创建
     * @param userId         当前用户id
     * @param bloomFilter    查询序列中当前用户的前一个用户的布隆过滤器，当前以前的节点已经在bloom过滤器中了，后面不能在继续使用
     */
    public CPINode(int userId, int weight, BloomFilter<Integer> bloomFilter) {
        this.userId = userId;
        this.weight = weight;
        nextLabelCandidateCPITreeNodeMap = new HashMap<Integer, CPITreeNode>();
        usedBloomFilter = new BloomFilter(bloomFilter);
        //将当前的用户id添加到布隆过滤器中，代表已经访问过
        usedBloomFilter.add(userId);
    }

    /**
     * 查找到一个用户，添加到索引路径中
     * @param userId    用户id
     * @param label     用户标签
     * @param weight    到达当前路径的
     * @param isLeaf
     * @return
     */
    public boolean addUser(int userId, int label, int weight, boolean isLeaf) {
        //当前节点已经处理过
        if(usedBloomFilter.contains(userId)) {
            return false;
        }
        CPITreeNode cpiTreeNode = null;
        //当前label类型不存在,创建一个树节点
        if(!nextLabelCandidateCPITreeNodeMap.containsKey(label)) {
            cpiTreeNode = new CPITreeNode(label, isLeaf);
            nextLabelCandidateCPITreeNodeMap.put(label, cpiTreeNode);
        }
        else {
            cpiTreeNode = nextLabelCandidateCPITreeNodeMap.get(label);
        }
        //向树节点中添加当前用户
        cpiTreeNode.add(new CPINode(userId, this.weight+weight, usedBloomFilter));
        return true;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BloomFilter getUsedBloomFilter() {
        return usedBloomFilter;
    }

    public void setUsedBloomFilter(BloomFilter usedBloomFilter) {
        this.usedBloomFilter = usedBloomFilter;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public HashMap<Integer, CPITreeNode> getNextLabelCandidateCPITreeNodeMap() {
        return nextLabelCandidateCPITreeNodeMap;
    }

    public void setNextLabelCandidateCPITreeNodeMap(HashMap<Integer, CPITreeNode> nextLabelCandidateCPITreeNodeMap) {
        this.nextLabelCandidateCPITreeNodeMap = nextLabelCandidateCPITreeNodeMap;
    }
}
