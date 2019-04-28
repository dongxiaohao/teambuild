package cn.neu.kou.teambuild.cpi;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 添加同层节点之间的约束，使节点能够同时访问同层的节点也能够访问下一层节点
 * @author : lq
 * @date : 2019/3/29
 */
public class CpiCorelateTreeNode extends CpiTreeNode{
    private ArrayList<CpiCorelateTreeNode> corelateNextNodeList;   // 与同层节点之间的约束（与当前节点具有关系的右边同层节点）
    private HashMap<Integer, CpiCorelateNode> corelateCandidateUserMap;   // 是当前标签类型且符合权重约束的候选用户节点，用户节点可能和之前的重复，但是不用管，在最后搜索的过程中进行过滤
    private CpiCorelateTreeNode preCorelateTreeNode;   //左父节点

    /**
     * 创建一个新的树节点
     * @param id          查询图中的id
     * @param label       节点标签
     * @param corelates   同层之间的限制
     */
    public CpiCorelateTreeNode(int id, int label, int corelates) {
        super(id, label);
        init(corelates);
    }

    /**
     * 创建一个树节点
     * @param id              查询图中id
     * @param label           节点标签
     * @param corelates       同层之间的限制
     * @param preTreeNode     父节点
     */
    public CpiCorelateTreeNode(int id, int label, int corelates, CpiTreeNode preTreeNode) {
        super(id, label, preTreeNode);
        init(corelates);
    }

    /**
     * 创建一个树节点
     * @param id              查询图中id
     * @param label           节点标签
     * @param corelates       同层之间的限制
     * @param preTreeNode     父节点
     * @param preCorelateTreeNode     左边父节点
     */
    public CpiCorelateTreeNode(int id, int label, int corelates, CpiTreeNode preTreeNode, CpiCorelateTreeNode preCorelateTreeNode) {
        super(id, label, preTreeNode);
        init(corelates);
        this.preCorelateTreeNode = preCorelateTreeNode;
    }

    /**
     * 初始化
     * @param corelates       同层之间的限制
     */
    private void init(int corelates) {
        //防止越界问题
        if(corelates < 0) {
            corelates = 0;
        }
        corelateNextNodeList = new ArrayList<>(corelates);
        corelateCandidateUserMap = new HashMap<>();
    }

    /**
     * 添加一个与当前节点具有约束关系的节点
     * @param cpiCorelateTreeNode
     */
    public void addCorelateTreeNode(CpiCorelateTreeNode cpiCorelateTreeNode) {
        this.corelateNextNodeList.add(cpiCorelateTreeNode);
    }

    /**
     * 添加一个与当前节点具有约束关系的用户节点
     * @param cpiCorelateNode
     */
    public void addCorelateCandidateUser(CpiCorelateNode cpiCorelateNode) {
        this.corelateCandidateUserMap.put(cpiCorelateNode.getUserId(), cpiCorelateNode);
    }

    /**
     * 判断当前节点右边是否还有和自己有约束的节点同层节点
     * @return
     */
    public boolean notExistsAfterSameLevelConstrant() {
        return this.corelateNextNodeList.size() == 0;
    }

    /**
     * 获取当前树节点中用户id为userId的CpiNode，如果不存在就创建（同层约束）
     * @param userId      用户id
     * @return            当前树节点中用户id为UserId的CpiNode
     */
    public CpiCorelateNode getOrCreateCorelateCpiNode(int userId) {
        CpiCorelateNode cpiCorelateNode;
        if(this.corelateCandidateUserMap.containsKey(userId)) {
            cpiCorelateNode = this.corelateCandidateUserMap.get(userId);
        }
        else {
            cpiCorelateNode = new CpiCorelateNode(userId, this.nextNodeList.size(), this.corelateNextNodeList.size());
            this.corelateCandidateUserMap.put(userId, cpiCorelateNode);
        }
        return cpiCorelateNode;
    }

    /**
     * 创建当前树节点中用户id为userId的CpiNode（同层约束）
     * @param userId  用户id
     */
    public void createCorelateCpiNode(int userId) {
        if(!this.corelateCandidateUserMap.containsKey(userId)) {
            this.corelateCandidateUserMap.put(userId, new CpiCorelateNode(userId, this.nextNodeList.size(), this.corelateNextNodeList.size()));
        }
    }

    public ArrayList<CpiCorelateTreeNode> getCorelateNextNodeList() {
        return corelateNextNodeList;
    }

    public void setCorelateNextNodeList(ArrayList<CpiCorelateTreeNode> corelateNextNodeList) {
        this.corelateNextNodeList = corelateNextNodeList;
    }

    public HashMap<Integer, CpiCorelateNode> getCorelateCandidateUserMap() {
        return corelateCandidateUserMap;
    }

    public void setCorelateCandidateUserMap(HashMap<Integer, CpiCorelateNode> corelateCandidateUserMap) {
        this.corelateCandidateUserMap = corelateCandidateUserMap;
    }

    public CpiCorelateTreeNode getPreCorelateTreeNode() {
        return preCorelateTreeNode;
    }

    public void setPreCorelateTreeNode(CpiCorelateTreeNode preCorelateTreeNode) {
        this.preCorelateTreeNode = preCorelateTreeNode;
    }
}
