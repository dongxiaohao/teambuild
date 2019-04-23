package cn.neu.kou.teambuild.cpi;


import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CPI索引树
 * @author : lq
 * @date : 2019/3/29
 */
public class CpiTree {
    private CpiTreeNode root;  //树的根节点
    private OriginGraphInterface originGraphInterface;   //原始图的操作接口
    private SearchGraphHelper searchGraphHelper;   //查询图的业务封装对象
    private HashMap<Integer, CpiTreeNode> treeNodeMap;  //为了快速查询定义的Map，同时也用于保证每个treenode只会被初始化一次
    public static int toltalCandidate = 0;
    public static int splitCandidate = 0;
    public static int restCandidate = 0;

    /**
     * CPI索引树构造函数，初始化头
     * @param originGraphInterface     //原始图的操作接口
     * @param searchInterfce        //查询图的业务封装对象
     */
    public CpiTree(OriginGraphInterface originGraphInterface, SearchGraphInterface searchInterfce) {
        this.originGraphInterface = originGraphInterface;
        this.searchGraphHelper = new SearchGraphHelper(searchInterfce);
        this.treeNodeMap = new HashMap<>();
    }

    /**
     * 构建CPI索引树
     */
    public void buildTree() {
        //初始化根节点
        int rootId = this.searchGraphHelper.getCurrentUserId();
        this.root = new CpiTreeNode(rootId, this.searchGraphHelper.getCurrentLabel());
        this.treeNodeMap.put(rootId, this.root);

        //递归创建所有的分支节点
        buildTreeNode(rootId, root);

        //找到根节点的所有候选用户并添加内节点
        findRootCandidateUser();

        //递归添加候选用户节点
        findCandidateUser(root);
    }

    /**
     * 删除所有具有空分支的节点
     */
    public void splitTree() {
        toltalCandidate = 0;
        splitCandidate = 0;
        restCandidate = 0;

        splitTree(this.root);

        System.out.println("totalCandidate: " + toltalCandidate);
        System.out.println("splitCandidate: " + splitCandidate);
        System.out.println("restCandidate: " + restCandidate);
    }

    /**
     * 删除以当前传入参数为根的树上的所有具有空分支的候选节点
     * @param cpiTreeNode    当前树的根
     */
    private void splitTree(CpiTreeNode cpiTreeNode) {

        //记录竖直方向上所有不具有空分支的候选用户节点
        Set<Integer> udKeepSet = new HashSet<>();
        if(cpiTreeNode.isLeaf()) {
            splitCandidate += cpiTreeNode.getCandidateUserMap().size();
            toltalCandidate += cpiTreeNode.getCandidateUserMap().size();
        }
        else {
            for (int userId : cpiTreeNode.getCandidateUserMap().keySet()) {
                if (cpiTreeNode.getCandidateUser(userId).isExistsEmptyBranch()) {
                    splitCandidate += 1;
                } else {
                    udKeepSet.add(userId);
                }
                toltalCandidate += 1;
            }
        }

        Set<Integer> lrKeepSet = new HashSet<>();
        //记录水平方向上所有不具有空分支的候选节点
        if(cpiTreeNode instanceof  CpiCorelateTreeNode) {
            CpiCorelateTreeNode cpiCorelateTreeNode = (CpiCorelateTreeNode) cpiTreeNode;
            if(cpiCorelateTreeNode.isLeaf()) {
                splitCandidate += cpiCorelateTreeNode.getCorelateCandidateUserMap().size();
                toltalCandidate += cpiCorelateTreeNode.getCorelateCandidateUserMap().size();
            }
            else {
                for (int userId : cpiCorelateTreeNode.getCorelateCandidateUserMap().keySet()) {
                    if (cpiCorelateTreeNode.getCorelateCandidateUserMap().get(userId).isExistsEmptyBranch()) {
                        splitCandidate += 1;
                    } else {
                        lrKeepSet.add(userId);
                    }
                    toltalCandidate += 1;
                }
            }

            //求两者的交集
            udKeepSet.retainAll(lrKeepSet);
        }

        //删除竖直方向的存在空分支的用户节点
        lrKeepSet.clear();
        lrKeepSet.addAll(cpiTreeNode.getCandidateUserMap().keySet());
        lrKeepSet.removeAll(udKeepSet);
        for(int userId : lrKeepSet) {
            cpiTreeNode.getCandidateUserMap().remove(userId);
        }

        restCandidate += cpiTreeNode.getCandidateUserMap().size();

        //清除水平方向上所有具有空分支的候选节点
        if(cpiTreeNode instanceof  CpiCorelateTreeNode) {
            CpiCorelateTreeNode cpiCorelateTreeNode = (CpiCorelateTreeNode) cpiTreeNode;
            lrKeepSet.clear();
            lrKeepSet.addAll(cpiCorelateTreeNode.getCorelateCandidateUserMap().keySet());
            lrKeepSet.removeAll(udKeepSet);
            for(int userId : lrKeepSet) {
                cpiCorelateTreeNode.getCorelateCandidateUserMap().remove(userId);
            }
            restCandidate += cpiCorelateTreeNode.getCandidateUserMap().size();
        }

        //递归清除
        for(CpiTreeNode child : cpiTreeNode.getNextNodeList()) {
            splitTree(child);
        }
//        if(cpiTreeNode instanceof  CpiCorelateTreeNode) {
//            CpiCorelateTreeNode cpiCorelateTreeNode = (CpiCorelateTreeNode) cpiTreeNode;
//            for(CpiTreeNode child : cpiCorelateTreeNode.getCorelateNextNodeList()) {
//                splitTree(child);
//            }
//        }
        
    }

    /**
     * 找到根节点的所有候选用户
     */
    private void findRootCandidateUser() {
    	System.out.println("current creat root cpinode: "+this.root.getLabel());
        List<Integer> candidateUserIdList = this.originGraphInterface.getUserWithLabel(root.getLabel());
        for(int id : candidateUserIdList) {
        	//System.out.println("current creat root cpinode: "+id);
            root.createCpiNode(id);
        }
    }

    /**
     * 从指定的根节点出发，搜索所有节点的候选匹配用户
     * @param root    指定的根节点
     */
    private void findCandidateUser(CpiTreeNode root) {
    	int nums=0;
    	System.out.println("current search nodeId: "+root.id);
        if(root.isLeaf()) {
            return;
        }

        CpiNode curUserNode;

        // 针对每个候选用户进行邻居搜索(竖直方向)
        for(Integer fromUserId : root.getCandidateUserMap().keySet()) {
        	//System.out.println("current search nodeId: "+fromUserId+" current search count:"+nums++);
            //当前树节点具有同层约束(水平方向)
            if(root instanceof CpiCorelateTreeNode) {
                CpiCorelateTreeNode corelateRoot = (CpiCorelateTreeNode) root;
                //获取当前用户处理节点，节点中具有上下层和同层约束，如果当前的用户节点不存在就创建
                curUserNode = corelateRoot.getOrCreateCorelateCpiNode(fromUserId);
                //以当前用户为中心，搜索同层邻居
                findSameLevelCandidateUserInternal(corelateRoot, (CpiCorelateNode) curUserNode);
            }

            //获取当前用户处理节点，处理节点中上下层约束
            curUserNode = root.getOrCreateCpiNode(fromUserId);
            //以当前用户为中心，搜索下层邻居
            findCandidateUserInternal(root, curUserNode);
        }

        // 递归查找候选集
        for(CpiTreeNode cpiTreeNode : root.getNextNodeList()) {
            findCandidateUser(cpiTreeNode);
        }
    }

    /**
     * 找到当前用户节点符合约束的下层邻居，并将到达节点id以及代价计入当前用户节点的对应分支中
     * @param root         当前用户节点所在的树节点
     * @param cpiNode      当前用户节点
     */
    private void findCandidateUserInternal(CpiTreeNode root, CpiNode cpiNode) {
        if(root.isLeaf()) {
            return;
        }
        float weight;
        for(CpiTreeNode currentChild : root.getNextNodeList()) {
            weight = this.searchGraphHelper.getWeight(root.getId(), currentChild.getId());
            List<Link> candidateList = null;

            candidateList = this.originGraphInterface.getAvailableNeighbors(cpiNode.getUserId(),weight, currentChild.getLabel());

            for (Link link : candidateList) {
                currentChild.createCpiNode(link.getToUserId()); //创建被指向节点
                cpiNode.add(currentChild.getId(), link.getToUserId(), link.getWeight());
            }
        }
    }

    /**
     * 找到当前用户节点符合约束的同层邻居，并将到达节点id以及代价计入当前用户节点的对应分支中
     * 默认将同层约束处理为从左到右，左边节点知道右边节点对其的约束，反过来不成立，右边节点仅知道部分左边节点对其有约束
     * @param root         当前用户节点所在的树节点
     * @param cpiNode      当前用户节点
     */
    private void findSameLevelCandidateUserInternal(CpiCorelateTreeNode root, CpiCorelateNode cpiNode) {
        if(root.notExistsAfterSameLevelConstrant()) {
            return;
        }
        float weight;
        for(CpiCorelateTreeNode currentChild : root.getCorelateNextNodeList()) {
            weight = this.searchGraphHelper.getWeight(root.getId(), currentChild.getId());
            List<Link> candidateList = this.originGraphInterface.getAvailableNeighbors(cpiNode.userId,
                    weight, currentChild.getLabel());

            for (Link link : candidateList) {
//                currentChild.createCorelateCpiNode(link.getToUserId()); //创建被指向节点
                cpiNode.addCorelate(currentChild.getId(), link.getToUserId(), link.getWeight());
            }
        }
    }

    /**
     * 每次创建当前树节点的下一层树节点，递归调用直到所有的树节点创建完成
     * @param curUserId   当前用户id
     * @param root        当前树节点
     */
    private void buildTreeNode(int curUserId, CpiTreeNode root) {
    	
    	System.out.println("current creat treeNode: "+curUserId);

        //获取当前节点的孩子节点
        HashSet<Integer> children = this.searchGraphHelper.getNotVisistNeighbor(curUserId, treeNodeMap.keySet());
        //不存在孩子节点，设置为叶子节点
        if(children.size() == 0) {
            root.setLeaf(true);
            return;
        }

        //找到孩子节点中具有相互关系的节点，优先创建
        HashMap<Integer, Set<Integer>> fromToMap = this.searchGraphHelper.getCorelateFromToMap(curUserId, children);

        /**
         * 创建具有同层约束的树节点,默认约束从左边指向右边
         */
        int id, label, corelates;
        CpiCorelateTreeNode fromCpiNode =  null;
        CpiCorelateTreeNode toCpiNode =  null;
        for(int from : fromToMap.keySet()) {
            //创建左边节点
            if(treeNodeMap.containsKey(from)) {
                fromCpiNode = (CpiCorelateTreeNode) treeNodeMap.get(from);
            }
            else {
                id = from;
                label = this.searchGraphHelper.getLabel(id);
                corelates = fromToMap.get(id).size();
                fromCpiNode = new CpiCorelateTreeNode(id, label, corelates, root, null);
                treeNodeMap.put(from, fromCpiNode);
                root.addTreeNode(fromCpiNode);
            }
            //循环创建左节点和所有右节点之间的联系
            for(int to : fromToMap.get(from)) {
                if (treeNodeMap.containsKey(to)) {
                    toCpiNode = (CpiCorelateTreeNode) treeNodeMap.get(to);
                }
                else {
                    id = to;
                    label = this.searchGraphHelper.getLabel(id);
                    if(fromToMap.get(id) == null) {
                        corelates = 0;
                    }
                    else {
                        corelates = fromToMap.get(id).size();
                    }
                    toCpiNode = new CpiCorelateTreeNode(id, label, corelates, root, fromCpiNode);
                    fromCpiNode.addCorelateTreeNode(toCpiNode);
                    treeNodeMap.put(to, toCpiNode);
                    root.addTreeNode(toCpiNode);
                }
            }
        }

        /**
         * 创建不存在同层约束的树节点
         */
        for(int userId : children) {
            if(treeNodeMap.containsKey(userId)) {
                continue;
            }
            else {
                CpiTreeNode temp = new CpiTreeNode(userId, this.searchGraphHelper.getLabel(userId), root);
                root.addTreeNode(temp);
                treeNodeMap.put(userId, temp);
            }
        }

        /**
         * 递归创建所有树节点
         */
//        CpiTreeNode cpiTreeNode;
//        for(int i=root.getNextNodeList().size()-1; i>=0 ;i--) {
//            cpiTreeNode = root.getNextNodeList().get(i);
//            buildTreeNode(cpiTreeNode.getId(), cpiTreeNode);
//        }
        for(CpiTreeNode node : root.getNextNodeList()) {
            buildTreeNode(node.getId(), node);
        }
    }

    /**
     * 返回树上一共有多少的节点
     * @return  树上一共有多少节点
     */
    public int getTreeNodeNum() {
        return this.treeNodeMap.size();
    }

    /**
     * 返回树的根节点
     * @return   树的根节点
     */
    public CpiTreeNode getRoot() {
        return this.root;
    }

    /**
     * 返回id对应的树节点
     * @param id   树节点id
     * @return     树节点
     */
    public CpiTreeNode getTreeNode(int id) {
        return this.treeNodeMap.get(id);
    }

    @Override
    public String toString() {
        return "CpiTree{" +
                "root=" + root +
                ", originGraphInterface=" + originGraphInterface +
                ", searchGraphHelper=" + searchGraphHelper +
                ", treeNodeMap=" + treeNodeMap +
                '}';
    }
    /**
     * 根据下一层删除当前层的所有不合法
     * @param cpiTreeNode
     */
    public void bottom_up(CpiTreeNode cpiTreeNode){
    	HashSet<Integer> all_childSet=new HashSet<Integer>();
    	HashSet<Integer> dele_cpinodeSet=new HashSet<Integer>();
    	HashSet<Integer> valid_childSet=new HashSet<>();
    	//目的：得到全部合法的孩纸节点集合
    	for(CpiTreeNode child : cpiTreeNode.getNextNodeList()) {
    		if(child.isLeaf) {	//经过top-down后叶子节点全部为合法节点，故球要远不加入合法集合中
    			
    			int child_searchId=child.id;  //得到叶子节点在原图中的Id	
    			//valid_childSet.addAll(cpiTreeNode.)
    			for(int node:cpiTreeNode.getCandidateUserMap().keySet()) {
    				//对当前层的每个节点，依次将对应的叶子节点的集合加入合法集合中
    				if(cpiTreeNode.getCandidateUser(node).getEachBranchCandidateIdWeightMap().isEmpty()) {
    					dele_cpinodeSet.add(node);
    					//System.out.println("this node: "+node+" this child: "+child_searchId);
    					
    				}
    					
    				else 
    					valid_childSet.addAll(cpiTreeNode.getCandidateUser(node).getEachBranchCandidateIdWeightMap().get(child_searchId).keySet());
    			}
    			
    			continue;		//如果下一层节点是叶子节点，由于已在top-down中删除，故不进行操作
    		}
    		else {	
    			//如果不是叶子节点，则将其合法的孩纸节点全部加入Set集合中
    			valid_childSet.addAll(child.getCandidateUserMap().keySet());
    		}
    	}
    	for(int remove_node:dele_cpinodeSet)
    		cpiTreeNode.getCandidateUserMap().remove(remove_node);
    	
    	
    	//HashSet<Integer> cpi_delSet=new HashSet<Integer>();
    	dele_cpinodeSet.clear();
    	for(int node:cpiTreeNode.getCandidateUserMap().keySet()) {
    		
    		//清空all_child集合
    		all_childSet.clear();
    		//将当前节点的全部子节点加入队列
    		all_childSet.addAll(cpiTreeNode.getCandidateUser(node).getAll_user());
    		//创建一个应删除的节点集合
    		HashSet<Integer> delte_childSet=new HashSet<Integer>();
    		//加入全部节点
    		delte_childSet.addAll(all_childSet);
    		//清除合法节点
    		delte_childSet.removeAll(valid_childSet);
    		//删除不合法节点
    		cpiTreeNode.getCandidateUser(node).remove_All(delte_childSet);
    		if(cpiTreeNode.getCandidateUser(node).new_ExistEmptyBrach()){
    			dele_cpinodeSet.add(node);
    			//cpiTreeNode.getCandidateUserMap().remove(node);
    		}
    		
    	}
    	for(int remove_node:dele_cpinodeSet)
    		cpiTreeNode.getCandidateUserMap().remove(remove_node);
    	
    	//cpiTreeNode.
	}
    
    public List<Integer> getSearch_seq(){
    	List<Integer> result=new ArrayList<Integer>();
    	result.add(this.searchGraphHelper.getCurrentUserId());
    	for(int i=0;i<result.size();i++) {
    		List<Integer> add_node=this.searchGraphHelper.getNeighborhood(result.get(i));
    		for(int node:add_node) {
    			if(!result.contains(node))
    				result.add(node);
    		}
    	}
    	for(int i:result)
    		System.out.print(i+" ");
		return result;
  }
    
    public void bottom_up() {
    	List<Integer> search_seq=this.getSearch_seq();
    	for(int i=search_seq.size()-1;i>=0;i--) {
    		if(!this.getTreeNode(search_seq.get(i)).isLeaf) {
    			
    			bottom_up(this.getTreeNode(search_seq.get(i)));
    			
    		}
    	}
    	
	}
}
