package cn.neu.kou.teambuild.cpi;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * CPI索引树上的节点
 * @author : lq
 * @date : 2019/3/29
 */
public class CpiTreeNode {
    protected int id;        //对应查询图中节点的id
    protected int label;     //当前节点的标签
    protected LinkedList<CpiTreeNode> nextNodeList;   //CPI树上的分支
    protected HashMap<Integer, CpiNode> candidateUserMap;  //是当前标签类型且符合权重约束的候选用户节点，用户节点可能和之前的重复，但是不用管，在最后搜索的过程中进行过滤
    protected CpiTreeNode preTreeNode;                //父节点
    protected boolean isLeaf;                         //是否是叶子节点
    protected boolean isVisist;                       //当前树节点是否已经被访问过


    /**
     * 创建一个新的树节点
     * @param id      查询图中id
     * @param label   标签值
     */
    public CpiTreeNode(int id, int label) {
        this.id = id;
        this.label = label;
        this.candidateUserMap = new HashMap<>();
        this.nextNodeList = new LinkedList<>();
        this.isLeaf = false;
    }

    /**
     * 创建一个树节点
     * @param id            查询图中id
     * @param label         标签值
     * @param preTreeNode   父节点
     */
    public CpiTreeNode(int id, int label, CpiTreeNode preTreeNode) {
        this(id, label);
        this.preTreeNode = preTreeNode;
    }

    /**
     * 添加一个数节点（分支）
     * @param cpiTreeNode   待添加的树节点
     */
    public void addTreeNode(CpiTreeNode cpiTreeNode) {
        this.nextNodeList.add(cpiTreeNode);
    }

    /**
     * 添加一个用户节点
     * @param cpiNode   待添加的用户节点
     */
    public void addUserNode(CpiNode cpiNode) {
        this.candidateUserMap.put(cpiNode.getUserId(), cpiNode);
    }

    /**
     * 获取当前树节点中用户id为userId的CpiNode，如果不存在就创建
     * @param userId      用户id
     * @return            当前树节点中用户id为UserId的CpiNode
     */
    public CpiNode getOrCreateCpiNode(int userId) {
        CpiNode cpiNode;
        if(this.candidateUserMap.containsKey(userId)) {
            cpiNode = this.candidateUserMap.get(userId);
        }
        else {
            cpiNode = new CpiNode(userId, this.nextNodeList.size());
            this.candidateUserMap.put(userId, cpiNode);
        }
        return cpiNode;
    }

    /**
     * 创建当前树节点中用户id为userId的CpiNode
     * @param userId      用户id
     */
    public void createCpiNode(int userId) {
        if(!this.candidateUserMap.containsKey(userId)) {
            this.candidateUserMap.put(userId, new CpiNode(userId, this.nextNodeList.size()));
        }
    }

    /**
     * 返回指定UserId的候选用户节点
     * @param candidateUserId    id
     * @return                   候选用户CpiNode
     */
    public CpiNode getCandidateUser(int candidateUserId) {
        return this.candidateUserMap.get(candidateUserId);
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public LinkedList<CpiTreeNode> getNextNodeList() {
        return nextNodeList;
    }

    public void setNextNodeList(LinkedList<CpiTreeNode> nextNodeList) {
        this.nextNodeList = nextNodeList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Integer, CpiNode> getCandidateUserMap() {
        return candidateUserMap;
    }

    public void setCandidateUserMap(HashMap<Integer, CpiNode> candidateUserMap) {
        this.candidateUserMap = candidateUserMap;
    }

    public CpiTreeNode getPreTreeNode() {
        return preTreeNode;
    }

    public void setPreTreeNode(CpiTreeNode preTreeNode) {
        this.preTreeNode = preTreeNode;
    }

    public boolean isVisist() {
        return isVisist;
    }

    public void setVisist(boolean visist) {
        isVisist = visist;
    }

}
