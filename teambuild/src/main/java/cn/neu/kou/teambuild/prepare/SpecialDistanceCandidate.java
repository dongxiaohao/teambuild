package cn.neu.kou.teambuild.prepare;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 此类为一个数据结构体，用于管理和组织距离节点指定距离的用户以及标签，
 * 将具有相同标签的节点组合在一起，通过标签不用遍历就能快速返回具有该类标签节点集合
 */
public class SpecialDistanceCandidate {
    private HashMap<Integer, Set<Integer>> labelIdsMap;  //标签与具有该标签的节点组成的map

    public SpecialDistanceCandidate() {
        this.labelIdsMap = new HashMap<Integer, Set<Integer>>();
    }

    /**
     * 添加一个具有某个标签的用户节点
     * @param label   标签
     * @param id      用户id
     */
    public void addIdWithLabel(int label, int id) {
        if(labelIdsMap.containsKey(label)) {
            labelIdsMap.get(label).add(id);
        }
        else {
            HashSet<Integer> idSet = new HashSet<Integer>();
            idSet.add(id);
            labelIdsMap.put(label, idSet);
        }
    }

    /**
     * 获取具有某个标签的用户集合
     * @param label   标签
     * @return        具有该标签的用户集合
     */
    public Set<Integer> getAtSpecialDistanceIdsWithLabel(int label) {
        if(labelIdsMap.containsKey(label)) {
            return labelIdsMap.get(label);
        }
        else {
            return new HashSet<Integer>();
        }
    }

    /**
     * 将字符串格式的数据转化为内存数据
     * @param data     数据字符串
     * @return         数据实例
     */
    public static SpecialDistanceCandidate fromString(String data) {
        SpecialDistanceCandidate specialDistanceCandidate = new SpecialDistanceCandidate();

        String[] eachLabels = data.split("!");
        for(String eachLab : eachLabels) {
            String[] labelIds = eachLab.split("-");
            int lable = Integer.parseInt(labelIds[0]);
            String[] ids = labelIds[1].split(",");
            for(String id : ids) {
                specialDistanceCandidate.addIdWithLabel(lable, Integer.parseInt(id));
            }
        }

        return specialDistanceCandidate;
    }

    @Override
    public String toString() {
        //重写toString方法便于进行数据序列化和烦序列化
        StringBuilder stringBuilder = new StringBuilder();
        for(Integer label : labelIdsMap.keySet()) {
            stringBuilder.append(label);
            stringBuilder.append("-");
            for(int id : labelIdsMap.get(label)) {
                stringBuilder.append(id);
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            stringBuilder.append("!");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }
}
