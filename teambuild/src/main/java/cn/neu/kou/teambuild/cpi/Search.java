package cn.neu.kou.teambuild.cpi;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.main.Config;

import java.util.*;

/**
 * CPI索引树中合法团队
 * 算法实现的思路：
 * @author : lq
 * @date : 2019/3/31
 */
public class Search {
    private CpiTree cpiTree;     //cpi索引树
    private Set<Integer> teamIdSet;     //团队成员id集合
    private HashMap<Integer, Integer> roleUserMap;     //角色（技能）与用户之间的映射
    private HashMap<Integer, Set<Link>> adjMap;  //邻接表
    private HashSet<ValidateTeam> validateTeamSet;  //合法团队集合
    private int teamNumbers;             //团队成员个数
    private float curWeight;             //组成当前团队需要的通信代价
    private Stack<SearchSnapShot> statusStack;   //状态栈
    private int maxResult;                //搜索到的团队个数大于此值后就不在搜索了
    private int edgeNum;
//    private int cudInd;                  //当前栈中操作的位置
//    private HashMap<Integer, Integer> changeMap;  //记录发生的替换，仅在树建立完成后（第一个团地找出来以后）才能使用

    public Search(CpiTree cpiTree, int maxResult) {
        this.cpiTree = cpiTree;
        this.teamIdSet = new HashSet<>();
        this.roleUserMap = new HashMap<>();
        this.adjMap = new HashMap<>();
        this.validateTeamSet = new HashSet<>();
        this.teamNumbers = cpiTree.getTreeNodeNum();

        this.statusStack = new Stack<>();
        this.maxResult = maxResult;
//        this.changeMap = new HashMap<>();
        findEdge(this.cpiTree.getRoot());
    }

    //计算总共有多少条边
    private void findEdge(CpiTreeNode root) {
        this.edgeNum += root.getNextNodeList().size();
        for(CpiTreeNode child : root.getNextNodeList()) {
            findEdge(child);
        }
        if(root instanceof CpiCorelateTreeNode) {
            this.edgeNum += ((CpiCorelateTreeNode) root).getCorelateNextNodeList().size();
        }
    }

    /**
     * 生成所有合法的团队
     */
    public void generateAllTeam() {
        CpiTreeNode root = this.cpiTree.getRoot();
        HashSet<Integer> set = new HashSet<>();
        int num = 0;
        int count=0;
        for(int userId : root.getCandidateUserMap().keySet()) {
        	//if(count++<30)
        	//	continue;
        	//else {
            //清除当前的所有状态，从头开始
            clearState();
           // System.out.println("root cadidate " + num++ +"usreId: "+userId);
            this.teamIdSet.add(userId);
            this.roleUserMap.put(root.getId(), userId);
            //添加第一个快照
            set.clear();
            set.add(userId);
            SearchSnapShot searchSnapShot = new SearchSnapShot(set, root, root.getId(), root.getId(), false, false);
            this.statusStack.push(searchSnapShot);

            //一棵树的合法团队
            generateOneTreeValidateTeamSet();

            //System.out.println("current team size:" + this.validateTeamSet.size());
            //避免无穷无尽的搜索
            if(this.maxResult <= this.validateTeamSet.size()) {
                break;
            }
        	}
        //}
}

    /**
     * 清除状态，主要用户清除第一层的数据
     */
    private void clearState() {
        this.statusStack.clear();
        this.teamIdSet.clear();
        this.roleUserMap.clear();
        this.adjMap.clear();
        this.curWeight = 0;
        this.clearVisist(this.cpiTree.getRoot());
    }

    /**
     * 清除访问状态
     * @param root   根节点
     */
    private void clearVisist(CpiTreeNode root) {
        root.setVisist(false);
        for(CpiTreeNode child : root.getNextNodeList()) {
            clearVisist(child);
        }
    }

    /**
     * 以根用户为起点生成包含当前节点的所有合法团队
     * 为了保证curInd的正确性，所有对其修改都只能出现在下面个这个方法中
     */
    private void generateOneTreeValidateTeamSet() {
        //以当前用户为起点找不到合法的团队
        if(!buildStack()) {
            return;
        }

        //添加第一个团队
        ValidateTeam team = new ValidateTeam(this.curWeight, this.roleUserMap, this.adjMap);
        addTeam(team);

        int curInd = this.statusStack.size()-1;
        SearchSnapShot curSearchSnap;
        boolean replaceOk = false;
        HashMap<Integer, Integer> changeMap = new HashMap<>();

        while(curInd > 0) {
            changeMap.clear();

            //找到第一个存在替换的快照节点
            do {
                curSearchSnap = this.statusStack.get(curInd);
                curInd -= 1;
            }
            while(curInd > 0 && !(replaceOk = replaceOneCandiate(curSearchSnap, changeMap)));

            //没有找到替代
            if(!replaceOk) {
                break;
            }

            curInd += 1; //加1才是当前已经处理完成快照坐标
            //所有快照都已经被处理了
            if(curInd == this.statusStack.size()-1) {
                team = new ValidateTeam(this.curWeight, this.roleUserMap, this.adjMap);
                if(!addTeam(team)) {
                    //避免无穷无尽的搜索
                    break;
                }
            }
            else {  //往前回滚了一下快照，需要在继续找加进去
                curInd += 1; //加一才是下一个需要处理快照

                //循环处理后面快照，直到遇到不满足的条件
                while(curInd < this.statusStack.size()) {
                    curSearchSnap = this.statusStack.get(curInd);
                    curInd += 1;
                    if (!addOneCandidate(curSearchSnap, changeMap)) {
                        curInd -= 2;  //减2，让curInd指向需要回滚处理的节点（一个1为先加，一个1为当前不行节点）
                        break;
                    }
                }

                //所有快照状态已经被正确处理
                if(curInd == this.statusStack.size()) {
                    //由于没有break跳出上面的循环,需要对curInd减一使其指向需要回滚的节点（此时回滚上一个状态就行）
                    curInd -= 1;

                    //添加当前的合法团队
                    team = new ValidateTeam(this.curWeight, this.roleUserMap, this.adjMap);
                    if(!addTeam(team)) {
                        //避免无穷无尽的搜索
                        break;
                    }
                }
            }
        }
    }

    /**
     * 过滤错误的团队（边数不对肯定就不对）
     * @param team
     * @return
     */
    private boolean addTeam(ValidateTeam team) {
      //  int curEdgeSum = 0;
      //  for(int key : team.getAdjMap().keySet()) {
       //     curEdgeSum += team.getAdjMap().get(key).size();
     //   }

        //if(curEdgeSum == this.edgeNum) {
            this.validateTeamSet.add(team);
            //避免无穷无尽的搜索
            if(this.maxResult <= this.validateTeamSet.size()) {
               return false;
            }
        //}
        return true;
    }

    /**
     * 添加一个候选者
     * @param searchSnapShot   当前处理快照
     * @return                 是否添加成功
     */
    private boolean addOneCandidate(SearchSnapShot searchSnapShot, HashMap<Integer, Integer> changeMap) {
        boolean find;
        int oldParentUserId;
        int beforeUserId;

        beforeUserId = searchSnapShot.getCurUserId();

        oldParentUserId = searchSnapShot.getParentUserId();
        if (changeMap.containsKey(oldParentUserId)) {
            searchSnapShot.setParentUserId(changeMap.get(oldParentUserId));
        }
        //重置快照状态
        searchSnapShot.reset();

        find = searchSnapShot.isNotNull();

        //当前处理快照是否有合法的结果，有就压入
        if (searchSnapShot.isNotNull()) {
            if(selectCandidate(searchSnapShot)) {
                //更新状态
                pushVisistStatus(searchSnapShot);
                //记录每次有效的修改
                changeMap.put(beforeUserId, searchSnapShot.getCurUserId());
                find = true;
            }
            else {
                //循环找完所有的候选
                while (searchSnapShot.replaceCandidate()) {
                    //当前候选者符合条件
                    if (selectCandidate(searchSnapShot)) {
                        //更新状态
                        pushVisistStatus(searchSnapShot);
                        //记录每次有效的修改
                        changeMap.put(beforeUserId, searchSnapShot.getCurUserId());
                    }
                }
                find = searchSnapShot.isNotNull();
            }
        }

        return find;
    }


    /**
     * 从当前栈中往回找一个
     * @return
     */
    private boolean replaceOneCandiate(SearchSnapShot searchSnapShot, HashMap<Integer, Integer> changeMap) {
        int beforeUserId;

        //记录被修改的用户id，其会影响所有以其为根的节点
        beforeUserId = searchSnapShot.getCurUserId();

        rollbackVisistStatus(searchSnapShot);

        //判断当前状态中是否存在可替换用户，并替换
        while(searchSnapShot.replaceCandidate() && !selectCandidate(searchSnapShot)) ;

        //当前树状态节点中用户可以被替换，找到了并替换
        if(searchSnapShot.isNotNull()){
            //重新压栈
            pushVisistStatus(searchSnapShot);

            //记录当前修改(后面所有以他为根的都要进行同样的修改)
            changeMap.put(beforeUserId, searchSnapShot.getCurUserId());
            return true;
        }
        return false;
    }

    /**
     * 构建完全栈
     */
    private boolean buildStack() {
        boolean buildOk  = true;
        CpiTreeNode curCpiTreeNode;
        CpiNode curUserCpiNode;

        if(this.statusStack.size() <= 0) {
            return false;
        }

        SearchSnapShot curSnap = this.statusStack.peek();
        //当前快照针对的树节点在当前情况下无法
        if(!curSnap.isNotNull()) {
            return false;
        }

        curCpiTreeNode = this.cpiTree.getTreeNode(curSnap.getBranch());
        curUserCpiNode = curCpiTreeNode.getCandidateUser(curSnap.getCurUserId());

//        if(curUserCpiNode == null) { //不算error，因为在下一层被剪枝了，通过再一次剪枝能够避免这个错误
//            System.err.println(Search.class.getName() + " buildStack() : curUserCpiNode is null" );
//        }
        //存在空分支（某些联系断了，一定不是合法的）
        if(curUserCpiNode == null || curUserCpiNode.getEachBranchCandidateIdWeightMap() == null || curUserCpiNode.isExistsEmptyBranch()) {
            return false;
        }

        //记录当前访问用户id
//        this.teamIdSet.add(curSnap.getCurUserId());

        //处理当前节点的每个分支
        for (int branchId : curUserCpiNode.getEachBranchCandidateIdWeightMap().keySet()) {
            CpiTreeNode childCpiTreeNode = this.cpiTree.getTreeNode(branchId);

            //创建一个快照
             SearchSnapShot childSnap = new SearchSnapShot(curUserCpiNode.getEachBranchCandidateIdWeightMap().get(branchId).keySet(),
                    curCpiTreeNode, curSnap.getCurUserId(), branchId, false, false);

             //单个分支创建函数（注释的写法一定是错误的，丢解）
//             if(!buildBranchStack(childCpiTreeNode, childSnap, curUserCpiNode)) {
//                 buildOk = false;
//                 break;
//             }

             //带回滚创建分支节点快照
             buildOk = rollbackBuildBranchStack(childCpiTreeNode, childSnap, curUserCpiNode);
            //回滚创建也不行，说明当前选中的节点不行，其存在一个分支建立不出来
            if(!buildOk) {
                break;
            }
        }

        //当前树节点具有同层约束
        if(buildOk && curCpiTreeNode instanceof CpiCorelateTreeNode ) {
            CpiCorelateTreeNode curCorelateCpiTreeNode = (CpiCorelateTreeNode) curCpiTreeNode;
            CpiCorelateNode curCorelateCpiNode = curCorelateCpiTreeNode.getCorelateCandidateUserMap().get(curSnap.getCurUserId());

//            if(curCorelateCpiNode == null) {
//                System.err.println(Search.class.getName() + ": curCorelateCpiNode is null!");
//            }
            
            
            //当且仅当当前节点有后续的水平分支时处理
            if(curCorelateCpiNode != null) { 
            	boolean split;
            	if(Config.IS_SPLIT_TREE) {
            		split = curCorelateCpiNode.isExistsEmptyBranch();
            	}
            	else {
            		split = false;
            	}
            	
            	if(!split && curCorelateCpiNode.getEachCorelateCandidateIdWeightMap() != null) {
                //处理当前节点的每个分支
                for (int branchId : curCorelateCpiNode.getEachCorelateCandidateIdWeightMap().keySet()) {
                    CpiTreeNode childCpiTreeNode = this.cpiTree.getTreeNode(branchId);

                    //创建一个快照
                    SearchSnapShot childSnap = new SearchSnapShot(curCorelateCpiNode.getEachCorelateCandidateIdWeightMap().get(branchId).keySet(),
                            curCpiTreeNode, curSnap.getCurUserId(), branchId, false, true);

//                    //单个分支创建函数
//                    if (!buildBranchStack(childCpiTreeNode, childSnap, curCorelateCpiNode)) {
//                        buildOk = false;
//                        break;
//                    }

                    //带回滚创建分支节点快照
                    buildOk = rollbackBuildBranchStack(childCpiTreeNode, childSnap, curCorelateCpiNode);
                    //回滚创建也不行，说明当前选中的节点不行，其存在一个分支建立不出来
                    if(!buildOk) {
                        break;
                    }
                }
            }
            }
        }
//
//        //如果子树创建失败了，则重选一个节点
//        if(!buildOk) {
//            buildOk = roolbackRebuildStack(curSnap.getCurUserId());
//        }

        return  buildOk;
    }

    /**
     * 带回滚创建分支节点快照
     * @param childCpiTreeNode    当前分支快照作用的树节点
     * @param childSnap           当前分支快照
     * @param curUserCpiNode      当前分支父用户节点
     * @return
     */
    private boolean rollbackBuildBranchStack(CpiTreeNode childCpiTreeNode, SearchSnapShot childSnap, CpiNode curUserCpiNode) {
        boolean buildOk = true;
        boolean beforeState = childCpiTreeNode.isVisist();
        //如果当前分支创建失败就进行回滚，直到创建成功，或者当前节点快照中已经没有了候选者
        if(!buildBranchStack(childCpiTreeNode, childSnap, curUserCpiNode)) {

            //循环找候选者替代，然后继续往下找，直到当前分支快照栈建立成功
            //首先回滚，删除当前节点的所有分支节点快照（从当前状态重头开始）
            //其次在当前分支根节点中找候选者替换
            while(childSnap.isNotNull() && (buildOk=popUntilAllChildRemove(childSnap.getCurUserId())) && childSnap.replaceCandidate()) {
                //回滚之前访问状态
                childCpiTreeNode.setVisist(beforeState);
                //尝试创建单个分支快照栈，
                if(buildBranchStack(childCpiTreeNode, childSnap, curUserCpiNode)) {
                    //建立成功就可以退出循环
                    break ;
                }
            }

            //所有可选的已经找完，通过判断当前节点快照中是否存在候选者来确定是否创建成功
            buildOk = (buildOk && childSnap.isNotNull());
        }

        return buildOk;
    }

    /**
     * 创建分支栈
     * @param curCpiTreeNode      当前树节点
     * @param curSearchSnapShot   当前树节点快照
     * @param parentCpiNode    当前选中的用户节点的父节点
     * @return
     */
    private boolean buildBranchStack(CpiTreeNode curCpiTreeNode, SearchSnapShot curSearchSnapShot, CpiNode parentCpiNode) {
        boolean buildOk = true;
        boolean isVisist = curCpiTreeNode.isVisist();

        //当前快照中存在能够满足前面约束的用户节点
        if(!selectCandidate(curSearchSnapShot, isVisist)) {

            while(curSearchSnapShot.replaceCandidate()) {
                if(selectCandidate(curSearchSnapShot, isVisist)) {
                    break;
                }
            }
        }
        if(curSearchSnapShot.isNotNull()) {
            //将状态快照压栈，同时更新当前状态
            pushVisistStatus(curSearchSnapShot, parentCpiNode, true);

            //非叶子节点，迭代将树快照节点压入栈中
            if (!curCpiTreeNode.isLeaf() && !isVisist) {
                buildOk = buildStack();
            }
        }
        else {
            buildOk = false;
        }

        return buildOk;
    }

    /**
     * 从当前快照中选择一个满足前面约束的用户节点
     * @param searchSnapShot    树节点状态快照
     * @return                  存在满足前面约束的用户节点时返回true，否则返回false
     */
    private boolean selectCandidate(SearchSnapShot searchSnapShot) {
        //如果当前树节点被访问过,那么只能选择已经在访问集合中的节点
        if (searchSnapShot.isOnlyCheck()) {
            //找到第一个在已经访问集合中的当前分支的可达用户节点
            while(searchSnapShot.isNotNull() && !teamIdSet.contains(searchSnapShot.getCurUserId())) {
                searchSnapShot.replaceCandidate();
            }
        }
        //如果当前节点没有被访问过，必须选择不在访问集合中的用户
        else {
            while(searchSnapShot.isNotNull() && teamIdSet.contains(searchSnapShot.getCurUserId())) {
                searchSnapShot.replaceCandidate();
            }
        }

        //当前快照可用
        return searchSnapShot.isNotNull();
    }

    /**
     * 从当前快照中选择一个满足前面约束的用户节点
     * @param searchSnapShot    树节点状态快照
     * @param isVisist          当前树节点是否访问过
     * @return                  存在满足前面约束的用户节点时返回true，否则返回false
     */
    private boolean selectCandidate(SearchSnapShot searchSnapShot, boolean isVisist) {
        //如果当前树节点被访问过,那么只能选择已经在访问集合中的节点
        if (isVisist) {
            //找到第一个在已经访问集合中的当前分支的可达用户节点
            while(searchSnapShot.isNotNull() && !teamIdSet.contains(searchSnapShot.getCurUserId())) {
                searchSnapShot.replaceCandidate();
            }
            //成功找到合法的用户节点（不同的路径汇合了）
            if(searchSnapShot.isNotNull()) {
                searchSnapShot.setOnlyCheck(true);
            }
        }
        //如果当前节点没有被访问过，必须选择不在访问集合中的用户
        else {
            while(searchSnapShot.isNotNull() && teamIdSet.contains(searchSnapShot.getCurUserId())) {
                searchSnapShot.replaceCandidate();
            }
        }

        //当前快照可用
        return searchSnapShot.isNotNull();
    }

    /**
     * 将当前的快照压入栈中，更新当前访问状态
     * @param searchSnapShot   树节点快照
     */
    private void pushVisistStatus(SearchSnapShot searchSnapShot) {
        CpiNode parentCpiUserNode;
        if(searchSnapShot.isHasCorelate()) {
            CpiCorelateTreeNode corelateTreeNode = (CpiCorelateTreeNode) searchSnapShot.getParent();
            parentCpiUserNode = corelateTreeNode.getCorelateCandidateUserMap().get(searchSnapShot.getParentUserId());
        }
        else {
            parentCpiUserNode = searchSnapShot.getParent().getCandidateUser(searchSnapShot.getParentUserId());
        }

        pushVisistStatus(searchSnapShot, parentCpiUserNode, false);
    }

    /**
     * 将当前的快照压入栈中，更新当前访问状态
     * @param searchSnapShot   树节点快照
     * @param parentCpiUserNode          被当前快照选中的用户节点
     * @param reallyPushIntoStack        是否将快照压入栈中
     */
    private void pushVisistStatus(SearchSnapShot searchSnapShot, CpiNode parentCpiUserNode, boolean reallyPushIntoStack) {
        int toUserId = searchSnapShot.getCurUserId();
        int fromUserId = searchSnapShot.getParentUserId();
        int branchId = searchSnapShot.getBranch();
        int label = this.cpiTree.getTreeNode(branchId).getLabel();
        float weight;

        //判断权重从哪边过来
        if(parentCpiUserNode instanceof CpiCorelateNode) {
            weight = ((CpiCorelateNode) parentCpiUserNode).getEachCorelateCandidateIdWeightMap().get(branchId).get(toUserId);
        }
        else {
            weight = parentCpiUserNode.getEachBranchCandidateIdWeightMap().get(branchId).get(toUserId);
        }

        //更新当前访问状态（在原来的基础上多增加了一条边、一条边的权重）
        this.addOneLink(fromUserId, new Link(toUserId, label, weight));
        this.curWeight += weight;

        //将快照压入栈中
        if(reallyPushIntoStack) {
            this.statusStack.push(searchSnapShot);
        }

        //非检查状态需要添加一个目的节点到集合中
        if(!searchSnapShot.isOnlyCheck()) {
            this.teamIdSet.add(toUserId);
            this.roleUserMap.put(searchSnapShot.getBranch(), toUserId);

            //更新访问状态为访问
            this.cpiTree.getTreeNode(searchSnapShot.getBranch()).setVisist(true);
        }
    }


    /**
     * 栈中弹出当前父节点的所有孩子节点状态（因为有一个孩子掉链子了，坏了所有人的前途）
     * @param parentTreeNodeId   父树节点的id
     * @return                   是否清空成功，有可能回不到指定的父节点，那么就说明错误，返回false
     */
    private boolean popUntilAllChildRemove(int parentTreeNodeId) {
        //直到找到相同的父节点
        while(this.statusStack.size() > 0 && this.statusStack.peek().getCurUserId() != parentTreeNodeId) {
            popVisistStatus();
        }
        popVisistStatus();  //当前状态也弹出等待再次创建时压入
//        while(this.statusStack.size() > 0 && this.statusStack.peek().getParentUserId() != parentTreeNodeId) {
//            popVisistStatus();
//        }

        return this.statusStack.size() > 0;
    }

    /**
     * 弹出一个候选的用户
     * @return
     */
    private SearchSnapShot popVisistStatus() {
        if(this.statusStack.size() == 0) {
            return null;
        }

        SearchSnapShot searchSnapShot = statusStack.pop();

        rollbackVisistStatus(searchSnapShot);

        return searchSnapShot;
    }

//    /**
//     * 回滚快照状态（撤销操作）
//     * @return  当前回滚的快照，如果快照中有其他可以替代的，那么又可以继续向前了
//     */
//    private SearchSnapShot rollbackVisistStatus() {
//        if(this.statusStack.size() <= 1 || this.cudInd > this.statusStack.size()) {
//            return null;
//        }
//        SearchSnapShot searchSnapShot = this.statusStack.get(this.cudInd-1);
//
//        rollbackVisistStatus(searchSnapShot);
//
//        return searchSnapShot;
//    }

    /**
     * 回滚快照状态（撤销操作）
     * @param searchSnapShot   树节点快照
     * @return
     */
    private SearchSnapShot rollbackVisistStatus(SearchSnapShot searchSnapShot) {
        int curId = searchSnapShot.getCurUserId();
        int parentId = searchSnapShot.getParentUserId();

//        //当前处理元素的位置减1
//        this.cudInd -= 1;

        //弹出边和权重即可
        removeOneLink(parentId, curId);

        //非检查节点需要同时删除当前节点（仅当存在一条路径回退到当前节点，那么此时需要自己清扫战场）
        if(!searchSnapShot.isOnlyCheck()) {
            this.teamIdSet.remove(curId);
            this.roleUserMap.remove(searchSnapShot.getBranch());

            //更新访问状态为未访问
            this.cpiTree.getTreeNode(searchSnapShot.getBranch()).setVisist(false);
        }

        return  searchSnapShot;
    }


    /**
     * 删除一条边，同时减去当前边的权重，边为有向的（从上到下，或者从左到右）
     * @param parentUserId    起始节点id
     * @param curUserId       结束节点id
     * @return                返回删除边是否成功
     */
    private boolean removeOneLink(int parentUserId, int curUserId) {
        boolean removeOk = false;
        Link link;

        //边不存在
        if (this.adjMap.get(parentUserId) == null || this.adjMap.get(parentUserId).isEmpty()) {
            return false;
        }

        //找到边并删除
        Iterator<Link> iterator = this.adjMap.get(parentUserId).iterator();
        while (iterator.hasNext()) {
            if ((link = iterator.next()).getToUserId() == curUserId) {
                this.curWeight -= link.getWeight();
                iterator.remove();
                removeOk = true;
            }
        }

        if(removeOk && this.adjMap.get(parentUserId).size() == 0) {
            this.adjMap.remove(parentUserId);
        }

        return removeOk;
    }


    /**
     * 添加一条边
     * @param fromId   边的起点
     * @param link     边实体
     */
    public void addOneLink(int fromId, Link link) {
        Set<Link> oneRowAdj;

        if(this.adjMap.containsKey(fromId)) {
            oneRowAdj = this.adjMap.get(fromId);
        }
        else {
            oneRowAdj = new HashSet<>();
            this.adjMap.put(fromId, oneRowAdj);
        }
        oneRowAdj.add(link);
    }

    public HashSet<ValidateTeam> getValidateTeamSet() {
        return validateTeamSet;
    }
    
    public void del_team(List<Integer> node) {
    	List<ValidateTeam> del_Teams=new ArrayList<ValidateTeam>();
    	int count=0;
    	for(ValidateTeam team:this.validateTeamSet) {
    		int[] userId=team.getUserIds();
    		for(int i=0;i<userId.length;i++) {
    			int flag=0;
    			for(int j:node) {
    				if(userId[i]==j) {
    					//System.out.println(del_Teams.size());
    					del_Teams.add(team);
    					flag=1;
    					break;
    				}
    				//System.out.println(userId[i]+" "+j);
    			}
    			if(flag==1)
    				break;
    		}
    		
    	}
    	
    	System.out.println(del_Teams.size());
    	
    	for(ValidateTeam team:del_Teams) {
    		this.validateTeamSet.remove(team);
    		/*
    		if(this.validateTeamSet.remove(team))
    				System.out.print(true);
    		else {
				System.out.print(false);
			}
			*/
    	}
    	
    }


    /**
     * 创建以userid为根的第一层树结构
     * @param userId   根节点获选用户id
     * @return         第一层是否创建成功
     */
//    private boolean buildFirstLevel(int userId) {
//        CpiTreeNode rootCpiTreeNode = this.cpiTree.getRoot();
//        CpiNode rootCpiUserNode = rootCpiTreeNode.getCandidateUser(userId);
//        for(int branchId : rootCpiUserNode.getEachBranchCandidateIdWeightMap().keySet()) {
//            CpiTreeNode childCpiTreeNode = this.cpiTree.getTreeNode(branchId);
//
//            //创建一个快照
//            SearchSnapShot childSnap = new SearchSnapShot(rootCpiUserNode.getEachBranchCandidateIdWeightMap().get(branchId).keySet(),
//                    rootCpiTreeNode, userId, branchId, false, childCpiTreeNode instanceof CpiCorelateTreeNode);
//
//            //快照压入栈中
//            pushVisistStatus(childSnap, rootCpiUserNode, true);
//
//            //某个分支断了
//            if(!childSnap.isNotNull()) {
//                return  false;
//            }
//        }
//        return true;
//    }

    /**
     *回滚并重建栈
     * @param parentId   需要回滚到哪个节点
     * @return
     */
//    private boolean roolbackRebuildStack(int parentId) {
//        boolean needTry = true;
//        SearchSnapShot curSnap;
//
//        //弹出栈中所有的孩子状态快照，找不到父状态，回滚失败
//        while(needTry && popUntilAllChildRemove(parentId)) {
//            curSnap = this.statusStack.peek();
//            //父状态中存在可以替换的用户节点
//            if(curSnap.replaceCandidate()) {
//                //建栈成功
//                if(buildStack()) {
//                    needTry = false;
//                }
//            }
//            else {
//                parentId = curSnap.getParentUserId();
////                break;
//            }
//        }
//
//        return !needTry;
//    }
}
