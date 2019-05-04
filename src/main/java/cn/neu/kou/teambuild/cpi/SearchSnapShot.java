package cn.neu.kou.teambuild.cpi;

import cn.neu.kou.teambuild.util.Util;

import java.util.HashSet;
import java.util.Set;

/**
 * 1. 由于每次在搜索的过程中每个树节点必须被取到，并且树节点中只能取一个用户节点，
 * 2. 为了记录之前已经搜索过的状态从而进行回溯，创建此类
 * 3. 由于树的结构，节点之间的联系被固定在水平和竖直两个方向，且节点之间默认存在单向联系（从上到下、从左到右）
 * 4. 虽然在树节点中记录了指向前驱树节点的指针，但是依然无法统一在当前节点回到前驱节点（竖直方向具有单亲关系能够保证一定能够回到前驱，
 * 但是水平方向中，不能保证只有单亲联系，所以无法利用统一的方法回到前驱）
 * 5. 基于第四点，站在父节点的角度创建当前节点（状态中，即需要保存父树节点信息）
 * @author : lq
 * @date : 2019/4/3
 */
public class SearchSnapShot {
    private Set<Integer> notVisistIdSet;  //当前节点中没有被访问过的节点id集合
    private CpiTreeNode parent;           //父树节点
    private int parentUserId;             //当前处理用户节点的上一个节点
    private int curUserId;                //当前处理的用户id
    private int branch;                   //当前处理的分支
    private boolean isOnlyCheck;          //是否为仅检查状态
    private boolean hasCorelate;          //是否具有同层约束
    private boolean isNotNull;            //当前状态还有替代吗？

    /**
     *创建状态
     * @param notVisistIdSet 未访问的id序列
     * @param parent        父树节点
     * @param parentUserId  当前处理的用户节点
     * @param branch        当前处理分支
     * @param isOnlyCheck   是否为仅检查状态
     * @param hasCorelate    是否是具有同层约束
     */
    public SearchSnapShot(Set<Integer> notVisistIdSet, CpiTreeNode parent, int parentUserId, int branch, boolean isOnlyCheck, boolean hasCorelate) {
        this.parent = parent;
        this.parentUserId = parentUserId;
        this.branch = branch;
        this.isOnlyCheck = isOnlyCheck;
        this.hasCorelate = hasCorelate;

        //重置没有访问过得id集合
        resetNotVisitors(notVisistIdSet);
    }

    /**
     * 重置状态
     */
    public void reset() {
        Set<Integer> notVisistIdSet = null;
        if(this.hasCorelate) {
            CpiCorelateTreeNode cpiCorelateTreeNode = (CpiCorelateTreeNode) this.parent;
            CpiCorelateNode cpiCorelateNode = cpiCorelateTreeNode.getCorelateCandidateUserMap().get(parentUserId);
            //简单处理方法bug方法，不可取，如果可以应该去定位错误
//            notVisistIdSet = cpiCorelateNode.getEachCorelateCandidateIdWeightMap().get(branch).keySet();
            if(cpiCorelateNode != null && cpiCorelateNode.getEachCorelateCandidateIdWeightMap() != null && cpiCorelateNode.getEachCorelateCandidateIdWeightMap().containsKey(branch)) {
                notVisistIdSet = cpiCorelateNode.getEachCorelateCandidateIdWeightMap().get(branch).keySet();
            }
            else {
                notVisistIdSet = new HashSet<>();
            }
        }
        else {
            CpiNode cpiNode = this.parent.getCandidateUser(parentUserId);
            //简单处理方法bug方法，不可取，如果可以应该去定位错误
//            notVisistIdSet = cpiNode.getEachBranchCandidateIdWeightMap().get(branch).keySet();
            if(cpiNode != null && cpiNode.getEachBranchCandidateIdWeightMap() != null && cpiNode.getEachBranchCandidateIdWeightMap().containsKey(branch)) {
                notVisistIdSet = cpiNode.getEachBranchCandidateIdWeightMap().get(branch).keySet();
            }
            else {
                notVisistIdSet = new HashSet<>();
            }
        }
        resetNotVisitors(notVisistIdSet);
    }

    /**
     * 重新设置没有访问节点id
     * @param notVisistIdSet
     */
    public void resetNotVisitors(Set<Integer> notVisistIdSet) {
        if(this.notVisistIdSet == null) {
            this.notVisistIdSet = new HashSet<>();
        }
        else {
            this.notVisistIdSet.clear();
        }
        Util.copySet(notVisistIdSet, this.notVisistIdSet);

        if(this.notVisistIdSet.size() > 0) {
            //替换候选者
            replaceCandidate();
            this.isNotNull = true;
        }
        else {
            this.isNotNull = false;
        }
    }

    /**
     *
     * 寻找一个当前节点的替代
     * @return    是否找到替代
     */
    public boolean replaceCandidate() {
        if(this.notVisistIdSet != null && this.notVisistIdSet.size() > 0) {
            this.curUserId = this.notVisistIdSet.iterator().next();
            this.notVisistIdSet.remove(this.curUserId);
        }
        else {
            this.isNotNull = false;
        }
        return  this.isNotNull;
    }

    public int getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(int parentUserId) {
        this.parentUserId = parentUserId;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    public Set<Integer> getNotVisistIdSet() {
        return notVisistIdSet;
    }

    public void setNotVisistIdSet(Set<Integer> notVisistIdSet) {
        this.notVisistIdSet = notVisistIdSet;
    }

    public CpiTreeNode getParent() {
        return parent;
    }

    public void setParent(CpiTreeNode parent) {
        this.parent = parent;
    }

    public int getCurUserId() {
        return curUserId;
    }

    public void setCurUserId(int curUserId) {
        this.curUserId = curUserId;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public boolean isOnlyCheck() {
        return isOnlyCheck;
    }

    public void setOnlyCheck(boolean onlyCheck) {
        isOnlyCheck = onlyCheck;
    }

    public boolean isHasCorelate() {
        return hasCorelate;
    }

    public void setHasCorelate(boolean hasCorelate) {
        this.hasCorelate = hasCorelate;
    }
}
