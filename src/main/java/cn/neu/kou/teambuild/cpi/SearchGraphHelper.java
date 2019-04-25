package cn.neu.kou.teambuild.cpi;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;
import cn.neu.kou.teambuild.interfaces.UserInfo;
import cn.neu.kou.teambuild.util.Util;

import java.util.*;

/**
 * 为了业务需求对查询图数据的抽象类，其中维护查询序列，节点的信息
 * @author : lq
 * @date : 2019/3/29
 */
public class SearchGraphHelper {

    private HashMap<Integer, UserInfo> userIdInfoMap;
    private List<Integer> searchSequence;    //查询序列
    private int currentSearchIndex;  //当前处理的用户id在查询序列中的下标
    private SearchGraphInterface searchGraphInterface;   //查询图操作的接口

    /**
     *传入查询图的操作接口用于从查询图中操作获取数据
     * @param searchGraphInterface 查询图的操作接口
     */
    public SearchGraphHelper(SearchGraphInterface searchGraphInterface) {
        this.userIdInfoMap = new HashMap<>();
        this.currentSearchIndex = 0;
        this.searchGraphInterface = searchGraphInterface;

        generateData();
    }

    /**
     * 生成查询序列，以及辅助查询邻居信息的HashMap
     */
    public void generateData() {
        //获取查询序列
        this.searchSequence = this.searchGraphInterface.getSearchSequence();

        //生成便于快速找到当前节点到直接邻居信息的HashMap
        getAdjLinkMap();
    }

    /**
     * 生成便于快速找到当前节点到直接邻居信息的HashMap
     */
    private void getAdjLinkMap() {
        //对查询图中每个用户节点，找到其直接邻居并生成Link结构，并用邻居的id作为key，link作为值保存在HashMap中
        for(int i=0; i<this.searchSequence.size(); i++) {
            //保存邻居信息
            List<Link> neighbors = this.searchGraphInterface.findNeighbors(searchSequence.get(i));
            HashMap<Integer, Link> tempMap = new HashMap<>();
            for(Link link : neighbors) {
                tempMap.put(link.getToUserId(), link);
            }
            UserInfo userInfo = new UserInfo(searchSequence.get(i), this.searchGraphInterface.getLabel(searchSequence.get(i)));
            userInfo.setAdjLinkMap(tempMap);
            userIdInfoMap.put(searchSequence.get(i), userInfo);
        }

        //补坑(查询分解丢节点),仅限于节点编号连续的情况
        for(int i=0; i<this.searchGraphInterface.getNodeNumber(); i++) {
            if(!userIdInfoMap.containsKey(i)) {
                //保存邻居信息
                List<Link> neighbors = this.searchGraphInterface.findNeighbors(i);
                HashMap<Integer, Link> tempMap = new HashMap<>();
                for (Link link : neighbors) {
                    tempMap.put(link.getToUserId(), link);
                }
                UserInfo userInfo = new UserInfo(i, this.searchGraphInterface.getLabel(i));
                userInfo.setAdjLinkMap(tempMap);
                userIdInfoMap.put(i, userInfo);
            }
        }
    }

    /**
     * 获取查询图中两个用户之间的权重
     * @param fromUserId
     * @param toUserId
     * @return
     */
    public float getWeight(int fromUserId, int toUserId) {
        return this.searchGraphInterface.getDirectWeight(fromUserId, toUserId);
    }

    /**
     * 返回当前正在查询处理用户的label
     * @return   正在查询处理用户的label
     */
    public int getCurrentLabel() {
        return this.userIdInfoMap.get(this.searchSequence.get(currentSearchIndex)).getLabel();
    }

    /**
     * 返回用户id为userId的节点的label
     * @param userId  用户id
     * @return
     */
    public int getLabel(int userId) {
        return this.userIdInfoMap.get(userId).getLabel();
    }

    /**
     * 返回当前正在查询处理用户的id
     * @return   正在查询处理用户的id
     */
    public int getCurrentUserId() {
        return this.searchSequence.get(currentSearchIndex);
    }

    /**
     * 获取当前正在查询处理用户的邻居信息
     * @return
     */
    public HashMap<Integer, Link> getCurrentUserNeighborLinks() {
        return this.userIdInfoMap.get(this.searchSequence.get(currentSearchIndex)).getAdjLinkMap();
    }

    /**
     * 获取当前正在查询处理用户的邻居信息
     * @param userId     用户id
     * @return           当前用户的邻居集
     */
    public HashMap<Integer, Link> getUserNeighborLinks(int userId) {
        return this.userIdInfoMap.get(userId).getAdjLinkMap();
    }

    /**
     * 获取当前查询处理节点具有几个邻居（具有几个邻居需要在CPI索引树中创建几个分支）
     * @return   当前查询处理节点具有的邻居个数
     */
    public int getCurrentUserNeighborNum() {
        return this.userIdInfoMap.get(this.searchSequence.get(currentSearchIndex)).getAdjLinkMap().size();
    }

    /**
     * 获取某个节点的分支个数（直接邻居的个数）
     * @param userId     用户id
     * @return           当前用户的分支个数（直接邻居的个数）
     */
    public int getNeighborNum(int userId) {
        return this.userIdInfoMap.get(userId).getAdjLinkMap().size();
    }


    /**
     * 获取用户的没有访问的邻居
     * @param userId           用户id
     * @param visistUserSet    已经访问过的id集合（不可修改）
     * @return                 用户没有访问的邻居集合
     */
    public HashSet<Integer> getNotVisistNeighbor(int userId, Set<Integer> visistUserSet ) {
        //首先获取到和当前节点连接的所有节点id集合
        Set<Integer> children= this.userIdInfoMap.get(userId).getAdjLinkMap().keySet();

        //不能直接修改原始的brothers Set，需要使用拷贝
        HashSet<Integer> set = new HashSet<>(children.size());
        Util.copyHashSet(children, set);

        //删除已经被访问的过的节点，这些节点属于上层节点
        set.removeAll(visistUserSet);

        return set;
    }

    /**
     * 获取id为userid的节点的下层节点(孩子节点)之间的相互关系
     * @param userId    用户id
     * @param childrenIdSet  已经访问的过的节点集合
     * @return          相互关系对组成的map，{formId : toId}
     */
    public HashMap<Integer, Set<Integer>> getCorelateFromToMap(int userId, Set<Integer> childrenIdSet) {
        //结果
        HashMap<Integer, Set<Integer>> fromToMap = new HashMap<>();

        //获取当前节点所有孩子节点
        HashSet<Integer> set = new HashSet<>(childrenIdSet.size());

        //对于同层中节点寻找其相互间关系
        for(int from : childrenIdSet) {
            //不能直接修改参数，因为是引用类型，会导致上层的HashMap出错，每次使用拷贝份数据处理
            Util.copyHashSet(childrenIdSet, set);

            //移除当前节点
            set.remove(from);
            //和当前用户的直接连接的邻居id set做交集，得到同层之间的约束
            set.retainAll(this.userIdInfoMap.get(from).getAdjLinkMap().keySet());

            if(set.size() >= 1) {
                for(int to : set) {
                    //有向图中的无序性，可以强制规定从小节点指向大节点，避免成环处理
                    if(from > to) {
                        int temp = from;
                        from = to;
                        to = temp;
                    }
                    //如果当前的节点对应的具有相互关系的节点集合没有被创建，则创建
                    if(!fromToMap.containsKey(from)) {
                        fromToMap.put(from, new HashSet<Integer>());
                    }
                    fromToMap.get(from).add(to);
                }
            }
        }

        return fromToMap;
    }

    /**
     * 返回查询中的最大边的值
     * @return
     */
    public int maxWeight() {
        int maxWeight = 0;
        for(int id : userIdInfoMap.keySet()) {
            UserInfo userInfo = userIdInfoMap.get(id);
            for(Integer neighbor : userInfo.getAdjLinkMap().keySet()) {
                Link link = userInfo.getAdjLinkMap().get(neighbor);
                if(link.getWeight() > maxWeight) {
                    maxWeight = (int) link.getWeight();
                }
            }

        }
        return maxWeight;
    }

    /**
     * 将当前的查询处理向后移动一位
     * @return 是否还有下一个
     */
    public boolean moveToNext() {
        if(++this.currentSearchIndex >= this.searchSequence.size()) {
            return false;
        }
        else {
            return true;
        }
    }

    public HashMap<Integer, UserInfo> getUserIdInfoMap() {
        return userIdInfoMap;
    }

    public void setUserIdInfoMap(HashMap<Integer, UserInfo> userIdInfoMap) {
        this.userIdInfoMap = userIdInfoMap;
    }

    public List<Integer> getSearchSequence() {
        return searchSequence;
    }

    public void setSearchSequence(List<Integer> searchSequence) {
        this.searchSequence = searchSequence;
    }

    public int getCurrentSearchIndex() {
        return currentSearchIndex;
    }

    public void setCurrentSearchIndex(int currentSearchIndex) {
        this.currentSearchIndex = currentSearchIndex;
    }

    public SearchGraphInterface getSearchGraphInterface() {
        return searchGraphInterface;
    }

    public void setSearchGraphInterface(SearchGraphInterface searchGraphInterface) {
        this.searchGraphInterface = searchGraphInterface;
    }
    public List<Integer> getNeighborhood(int node) {
		List<Link> mem=searchGraphInterface.findNeighbors(node);
		List<Integer> result=new ArrayList<Integer>();
		for(Link link:mem) {
			result.add(link.getToUserId());		
		}
		return result;
	}
    /**
     * 
     * @param label
     * @return
     */
    public List<Integer> getnodewithlabel(int label) {
    	List<Integer> result=new ArrayList<Integer>();
    	int count=0;
    	for(int i:this.searchGraphInterface.getSeq_label()) {
    		if(i==label)
    			result.add(count++);
    		else
    			count++;
    	}
		return result;
	}
}
