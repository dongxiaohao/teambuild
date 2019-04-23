package cn.neu.kou.teambuild.core;

import java.util.Arrays;

/**
 * CPI搜索树类
 * @author lq
 * @date 2019-3-29
 */
public class CPITree {
    private CPITreeNode root;   //cpi搜索树的根节点
    private int teamNum;        //团队的人数
    private int[] teamMembers;  //团队的组成人员
    private int minCost;        //当前CPI搜索树中的最小花费

    /**
     * CPIso索引树构建
     * @param teamNum    团队人数
     */
    public CPITree(int teamNum) {
        this.teamNum = teamNum;
        this.teamMembers = new int[teamNum];
        this.minCost = Integer.MAX_VALUE;
    }

    /**
     * 找到花费最小的团队（带剪枝）
     * @return    花费最小团队的成员
     */
    public int[] findMinCostTeam(boolean isUpdateCpiTreeNoteMin) {
        int searchPaths[] = new int[teamNum];
        findMinCostTeamInternal(root, searchPaths, 0);
        if(isUpdateCpiTreeNoteMin) {
            CPITreeNode.CUR_MIN = minCost;
        }
        return teamMembers;
    }

    /**
     * 递归搜索花费最小的团队（带剪枝的版本）
     * @param root             当前遍历的树节点
     * @param searchPaths      当前已经搜索过的用户集合
     * @param loc              当前节点在树上属于第几层
     */
    private void findMinCostTeamInternal(CPITreeNode root, int[] searchPaths, int loc) {
        for(CPINode node : root.getCandidateList()) {
            searchPaths[loc] = node.getUserId();
            //找到了叶子节点，并且当前团队的花费比之前团队的要小，更新局部最优团队
            if(loc == teamNum-1 && node.getWeight() < this.minCost) {
                this.minCost = node.getWeight();
                this.teamMembers = Arrays.copyOf(searchPaths, teamNum);
                continue;
            }
            //当前团队的花费已经大于局部最小花费，直接进行剪枝
            if(node.getWeight() > this.minCost) {
                continue;
            }
            else {
                //递归搜索当前树节点中的用户节点所指向的所有的下一层树节点
                for(CPITreeNode treeNode : node.getNextLabelCandidateCPITreeNodeMap().values()) {
                    findMinCostTeamInternal(treeNode, searchPaths, loc+1);
                }
            }
        }
    }

    /**
     * 找到花费最小的团队（使用CPITreeNode中存放最小花费团队的花费进行剪枝）
     * 注意：一定要保证CPITreeNode中的CUR_MIN存放着当前所有合法团队中的最小花费
     * @return    花费最小团队的成员
     */
    public int[] findMinCostTeamWithMinCut() throws Exception {
        int searchPaths[] = new int[teamNum];
        if(!findMinCostTeamInternalWithMinCut(root, searchPaths, 0)) {
            throw new Exception("Exception：没有找到和当前最小花费对应的团队，请使用findMinCostTeam方法进行搜索");
        }
        this.minCost = root.CUR_MIN;
        return teamMembers;
    }

    /**
     * 递归搜索花费最小的团队（使用CPITreeNode中存放最小花费团队的花费进行剪枝）
     * 注意：一定要保证CPITreeNode中的CUR_MIN存放着当前所有合法团队中的最小花费
     * @param root             当前遍历的树节点
     * @param searchPaths      当前已经搜索过的用户集合
     * @param loc              当前节点在树上属于第几层
     */
    private boolean findMinCostTeamInternalWithMinCut(CPITreeNode root, int[] searchPaths, int loc) {
        for(CPINode node : root.getCandidateList()) {
            searchPaths[loc] = node.getUserId();
            //找到了叶子节点，并且是最小花费的团队
            if(loc == teamNum-1 && node.getWeight() == CPITreeNode.CUR_MIN) {
                this.teamMembers = Arrays.copyOf(searchPaths, teamNum);
                return true;
            }
            //当前团队的花费已经大于全局最小花费，直接进行剪枝
            if(node.getWeight() > CPITreeNode.CUR_MIN) {
                continue;
            }
            else {
                //递归搜索当前树节点中的用户节点所指向的所有的下一层树节点
                for(CPITreeNode treeNode : node.getNextLabelCandidateCPITreeNodeMap().values()) {
                    //如果找到，打断搜索
                    if(findMinCostTeamInternalWithMinCut(treeNode, searchPaths, loc+1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
