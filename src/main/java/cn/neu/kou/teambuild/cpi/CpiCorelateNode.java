package cn.neu.kou.teambuild.cpi;

import java.util.HashMap;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public class CpiCorelateNode extends CpiNode{
    /**
     *Map数组用于存放同层节点的中具有的相互关系
     */
    protected HashMap<Integer, HashMap<Integer, Float>> eachCorelateCandidateIdWeightMap;

    public CpiCorelateNode(int userId, int branchSize, int corelateSize) {
        super(userId, branchSize);
        //从水平方向上看，当且仅当当前节点为非叶子节点才有创建横向联系的必要
        if(corelateSize > 0) {
            eachCorelateCandidateIdWeightMap = new HashMap<>();
        }
        //这个地方有bug，bug的原因是CpiNode和CpiCorelateNode是不能同时具有两个身份的
        this.emptyBranchSize = corelateSize;
    }

    /**
     * 向候选用户集中添加一个新用户(同层约束)
     * @param whichCorelate      与哪个树节点的关系，值为树节点的id
     * @param userId           当前用户id
     * @param weight           到当前用户的权重
     */
    public void addCorelate(int whichCorelate, int userId, float weight) {
        if(!this.eachCorelateCandidateIdWeightMap.containsKey(whichCorelate)) {
            this.eachCorelateCandidateIdWeightMap.put(whichCorelate, new HashMap<Integer, Float>());
        }
        if(this.eachCorelateCandidateIdWeightMap.get(whichCorelate).size() == 0) {
            this.emptyBranchSize -= 1;
        }
        this.eachCorelateCandidateIdWeightMap.get(whichCorelate).put(userId, weight);
    }

    public HashMap<Integer, HashMap<Integer, Float>> getEachCorelateCandidateIdWeightMap() {
        return eachCorelateCandidateIdWeightMap;
    }

    public void setEachCorelateCandidateIdWeightMap(HashMap<Integer, HashMap<Integer, Float>> eachCorelateCandidateIdWeightMap) {
        this.eachCorelateCandidateIdWeightMap = eachCorelateCandidateIdWeightMap;
    }

//    @Override
//    public boolean isExistsEmptyBranch() {
//        if(emptyBranchSize != 0) {
//            return false;
//        }
//        if(this.eachCorelateCandidateIdWeightMap == null || this.eachCorelateCandidateIdWeightMap.size() == 0) {
//            return true;
//        }
//        return false;
//    }
}
