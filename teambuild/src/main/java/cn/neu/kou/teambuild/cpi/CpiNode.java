package cn.neu.kou.teambuild.cpi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public class CpiNode {
    protected int userId;
    /**
     *Map数组用于存放当前节点的每个分支的结果
     */
    protected HashMap<Integer, HashMap<Integer, Float>> eachBranchCandidateIdWeightMap;
    protected int emptyBranchSize;   //如果节点的分支存在为空，那么必然是一个不合法的结果

    public CpiNode(int userId, int branchSize) {
        this.userId = userId;
        //当且仅当当前节点为非叶子节点才有创建保存下一层指向数组
        if(branchSize > 0) {
            this.eachBranchCandidateIdWeightMap = new HashMap<>();
        }
        this.emptyBranchSize = branchSize;
    }

    /**
     * 向候选用户集中添加一个新用户
     * @param whichBranch      与哪个树节点的关系，值为树节点的id
     * @param userId           当前用户id
     * @param weight           到当前用户的权重
     */
    public void add(int whichBranch, int userId, float weight) {
        if(!this.eachBranchCandidateIdWeightMap.containsKey(whichBranch)) {
            this.eachBranchCandidateIdWeightMap.put(whichBranch, new HashMap<Integer, Float>());
        }
        if(this.eachBranchCandidateIdWeightMap.get(whichBranch).size() == 0) {
            this.emptyBranchSize -= 1;
        }
        this.eachBranchCandidateIdWeightMap.get(whichBranch).put(userId, weight);
    }

    /**
     * 所有的约束都满足，从树上来说就是每个分支都能找到一个候选者
     * @return
     */
    public boolean isExistsEmptyBranch() {
//        if(emptyBranchSize != 0) {
//            return true;
//        }
//
//        if(this.eachBranchCandidateIdWeightMap == null || this.eachBranchCandidateIdWeightMap.size() == 0) {
//            return true;
//        }
//        return false;
        return this.emptyBranchSize != 0;
    }
    public boolean new_ExistEmptyBrach() {
    	for(int searchid:this.eachBranchCandidateIdWeightMap.keySet()) {
    		if(this.eachBranchCandidateIdWeightMap.get(searchid).isEmpty())
    			return true;
    	}
    	return false;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public HashMap<Integer, HashMap<Integer, Float>> getEachBranchCandidateIdWeightMap() {
        return eachBranchCandidateIdWeightMap;
    }

    public void setEachBranchCandidateIdWeightMap(HashMap<Integer, HashMap<Integer, Float>> eachBranchCandidateIdWeightMap) {
        this.eachBranchCandidateIdWeightMap = eachBranchCandidateIdWeightMap;
    }

    public int getEmptyBranchSize() {
        return emptyBranchSize;
    }

    public void setEmptyBranchSize(int emptyBranchSize) {
        this.emptyBranchSize = emptyBranchSize;
    }
    /**
     * 返回所有的原图的子节点的集合
     * @return
     */
    public Set<Integer> getAll_user(){
    	HashSet<Integer> result=new HashSet<Integer>();
    	for(int user:this.getEachBranchCandidateIdWeightMap().keySet()) {
    		result.addAll(this.getEachBranchCandidateIdWeightMap().get(user).keySet());
    	}
    	return result;
		
	}
    
    public void remove_All(HashSet<Integer> del_set) {
    	for(int del_originId:del_set) {
    	for(int search:this.getEachBranchCandidateIdWeightMap().keySet()) {
    		this.getEachBranchCandidateIdWeightMap().get(search).remove(del_originId);
    	}
    }
	}


}
