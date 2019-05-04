package cn.neu.kou.teambuild.interfaces;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 两个节点之间的锚定
 * @author : lq
 * @date : 2019/3/29
 */
public class Link {
    private int toUserId;   //连接的用户节点id
    private int label;      //连接的用户label类型
    private float weight;     //到此用户的代价

    /**
     * 创建一个锚定
     * @param toUserId   连接的用户节点id
     * @param label      连接的用户的label类型
     * @param weight     到此用户的代价
     */
    public Link(int toUserId, int label, float weight) {
        this.toUserId = toUserId;
        this.label = label;
        this.weight = weight;
    }

    /**
     * 返回这条边代表的邻居id
     * @return   邻居id
     */
    public int getToUserId() {
        return toUserId;
    }

    /**
     * 拷贝一份
     * @return  副本对象
     */
    public Link copyLink(){
        Link link = new Link(this.getToUserId(), this.label, this.getWeight());
        return link;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
    public List<Link> sort_byweight(List<Link> list) {
    	Collections.sort(list,new Comparator<Link>() {
    		@Override
    		public int compare(Link l1,Link l2) {
    			return (int)l1.getWeight()-(int )l2.getWeight();
    		}
		});
		return list;
	}
}
