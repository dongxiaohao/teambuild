package cn.neu.kou.teambuild.cpi;

import cn.neu.kou.teambuild.interfaces.Link;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.*;

/**
 * 合法的团队
 * @author : lq
 * @date : 2019/4/2
 */
public class ValidateTeam {
    private HashMap<Integer, Integer> roleIdUserIdMap;                  //角色与用户id映射
    private HashMap<Integer, Set<Link>> adjMap;  //邻接表
    private float totalWeight;

    /**
     * 创建一个空的团队
     * @param length   团队的人数
     */
    public ValidateTeam(int length) {
        this.roleIdUserIdMap = new HashMap<>();
        this.adjMap = new HashMap<>();
    }

    /**
     * 创建一个完成的团队
     * @param totalWeight    总通信代价
     * @param roleIdUserIdMap        用户id与用户id映射
     * @param adjMap         团队中已有成员之间的邻接表
     */
    public ValidateTeam(float totalWeight, HashMap<Integer, Integer> roleIdUserIdMap, HashMap<Integer,Set<Link>> adjMap) {
        cloneTeam(totalWeight, roleIdUserIdMap, adjMap);
    }

    /**
     * 添加一个到用户到当前团队中
     * @param fromId    出发用户id
     * @param toId      目的用户id
     * @param weight    到用户的权重
     */
    public void addOneUser(int fromRoleId, int fromId, int toRoleId, int toId, int label, float weight) {
        this.roleIdUserIdMap.put(fromRoleId, fromId);
        this.roleIdUserIdMap.put(toRoleId, toId);
        this.totalWeight += weight;

        Link link = new Link(toId, label, weight);
        Set<Link> edgeSet;
        if(this.adjMap.containsKey(fromId)) {
            edgeSet = this.adjMap.get(fromId);
        }
        else {
            edgeSet = new HashSet<>();
            this.adjMap.put(fromId, edgeSet);
        }
        edgeSet.add(link);
    }

    /**
     * 利用当前的数据创建一个Team
     * @param weight        总权重
     * @param roleIdUserIdMap        用户id与用户id映射
     * @param adjMap        邻接表
     */
    public void cloneTeam(float weight, HashMap<Integer, Integer> roleIdUserIdMap, HashMap<Integer, Set<Link>> adjMap) {
        this.totalWeight = weight;
        this.roleIdUserIdMap = new HashMap<>();
        this.adjMap = new HashMap<>();
        copyIntMap(roleIdUserIdMap, this.roleIdUserIdMap);
        copyAdjMap(adjMap, this.adjMap);
    }

    /**
     * 拷贝Map
     * @param originMap   源Map
     * @param toMap       目标Map
     */
    public static  void copyIntMap(HashMap<Integer, Integer> originMap, HashMap<Integer, Integer> toMap) {
        for(int key :originMap.keySet()) {
            toMap.put(key, originMap.get(key));
        }
    }

    /**
     * 拷贝整型集合
     * @param origin   源集合
     * @param target   目标集合
     */
    public static void copyIntSet(Set<Integer> origin, Set<Integer> target) {
        for(Integer e : origin) {
            target.add(e);
        }
    }

    /**
     * 拷贝邻接表
     * @param origin   源邻接表
     * @param target   目的邻接表
     */
    public static void copyAdjMap(HashMap<Integer, Set<Link>> origin, HashMap<Integer, Set<Link>> target){
        Set<Link> originNeighbors;
        Set<Link> targetNeighbors;

        for(int fromId : origin.keySet()) {
            originNeighbors = origin.get(fromId);
            targetNeighbors = new HashSet<>();
            target.put(fromId, targetNeighbors);

            for(Link link : originNeighbors) {
                targetNeighbors.add(link.copyLink());
            }
        }
    }

    /**
     * 获取团队的id数组
     * @return   团队id数组
     */
    public int[] getUserIds() {
        int[] ids = new int[this.roleIdUserIdMap.size()];
        int ind = 0;
        for(int id : this.roleIdUserIdMap.values()) {
            ids[ind++] = id;
        }
        return ids;
    }

    /**
     * 判断与角色对应的边是否存在，存在时返回距离
     * @param fromRoleId    角色起始id
     * @param toRoleId      角色终止id
     * @return              用户间对应边距离
     */
    public int edgeWeight(int fromRoleId, int toRoleId) {
        if(!this.roleIdUserIdMap.containsKey(fromRoleId) || !this.roleIdUserIdMap.containsKey(toRoleId) ) {
            return 0;
        }
        int from = this.roleIdUserIdMap.get(fromRoleId);
        int to = this.roleIdUserIdMap.get(toRoleId);

        if(adjMap.containsKey(from)) {

        }
        else if(adjMap.containsKey(to)) {
            int temp = from;
            from = to;
            to = temp;
        }
        else {
            return 0;
        }

        for(Link link : adjMap.get(from)) {
            int linkToId = link.getToUserId();
            if(linkToId == to) {
                return (int) (link.getWeight()+0.5);
            }
        }

        return 0;
    }

    /**
     * 边的个数
     * @return
     */
    public int edgeSum() {
        int sum = 0;
        for(int key : adjMap.keySet()) {
            sum += adjMap.get(key).size();
        }
        return sum;
    }

    public HashMap<Integer, Integer> getRoleIdUserIdMap() {
        return roleIdUserIdMap;
    }

    public void setRoleIdUserIdMap(HashMap<Integer, Integer> roleIdUserIdMap) {
        this.roleIdUserIdMap = roleIdUserIdMap;
    }

    public HashMap<Integer, Set<Link>> getAdjMap() {
        return adjMap;
    }

    public void setAdjMap(HashMap<Integer, Set<Link>> adjMap) {
        this.adjMap = adjMap;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(float totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * 带权重的边（未使用）
     */
    public static class EdgeWithWeight {
        private int fromId;   //源端点id
        private int toInd;    //目的端点id
        private float weight; //边上权重

        /**
         * 创建一条边
         * @param fromId    源端点id
         * @param toInd     目的端点id
         * @param weight    权重
         */
        public EdgeWithWeight(int fromId, int toInd, float weight) {
            this.fromId = fromId;
            this.toInd = toInd;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            return this.fromId*100000 + toInd;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            else if(obj instanceof EdgeWithWeight) {
                EdgeWithWeight edge = (EdgeWithWeight) obj;
                if(edge.getFromId() == this.getFromId() && edge.getToInd() == this.getToInd()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        @Override
        protected Object clone(){
            EdgeWithWeight edge = new EdgeWithWeight(this.fromId, this.toInd, this.weight);
            return edge;
        }

        public int getFromId() {
            return fromId;
        }

        public void setFromId(int fromId) {
            this.fromId = fromId;
        }

        public int getToInd() {
            return toInd;
        }

        public void setToInd(int toInd) {
            this.toInd = toInd;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }
}
