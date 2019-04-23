package cn.neu.kou.teambuild.core;

import java.util.LinkedList;

/**
 * CPI索引树节点类
 * @author LQ
 * @date 2019-3-29
 */
public class CPITreeNode {
    public static int CUR_MIN = Integer.MAX_VALUE;   //维护当前合法的组团方案的最小权重和，利用其进行剪枝
    private int label; // 当前标签类别，即当前需要找什么标签的用户
    private LinkedList<CPINode> candidateList;    //具有相同标签的，并且和上一个用户节点在限定权重和路径内能够到达的节点集合
    private boolean isLeaf;         //是否是叶子节点，叶子节点代表团队已经组建完成

    /**
     * CPI索引树中的节点构造函数
     * @param label        当前节点的label
     * @param isLeaf       当前节点是否是叶子节点
     */
    public CPITreeNode(int label, boolean isLeaf) {
        this.candidateList = new LinkedList<CPINode>();
        this.isLeaf = isLeaf;
    }

    /**
     * 向CPI索引树节点中添加一个候选用户，在添加的同时进行了剪枝，避免了权重和大于当前最小权重的部分还进行搜索
     * @param cpiNode   添加的候选用户
     * @return          用户是否添加成功，
     */
    public boolean add(CPINode cpiNode) {
        //权重和大于当前最小权重，剪枝
        if(cpiNode.getWeight() < CUR_MIN) {
            return false;
        }
        //是叶子节点，并且当前路径的权重和小于之前最优，那么更新最小权重和
        else if(isLeaf) {
            CUR_MIN = cpiNode.getWeight();
        }
        this.candidateList.add(cpiNode);
        return true;
    }

    /**
     * 清除当前的最小权重组合方案的 权重，以便重新构建树
     */
    public static void clearMax() {
        CUR_MIN = Integer.MAX_VALUE;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public LinkedList<CPINode> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(LinkedList<CPINode> candidateList) {
        this.candidateList = candidateList;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }
}
