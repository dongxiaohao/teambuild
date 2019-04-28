package test.buildcpitree;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/30
 */
public class OriginGraphTest implements OriginGraphInterface {

    private float[][] graph = {
            // v0   v1    v2   v3     v4    v5    v6    v7    v8    v9    v10   v11  v12
            {0.0f, 1.3f, 2.5f, 2.5f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v0
            {1.3f, 0.0f, 0.0f, 0.0f, 1.5f, 0.0f, 0.7f, 2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v1
            {2.5f, 0.0f, 0.0f, 2.5f, 0.0f, 2.0f, 0.0f, 3.0f, 1.8f, 0.0f, 0.0f, 0.0f, 0.0f}, // v2
            {2.5f, 0.0f, 2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f}, // v3
            {3.0f, 1.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f, 0.0f}, // v4
            {0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v5
            {0.0f, 0.7f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v6
            {0.0f, 2.5f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.6f, 1.0f, 0.0f}, // v7
            {0.0f, 0.0f, 1.8f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5f}, // v8
            {0.0f, 0.0f, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v9
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v10
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, // v11
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5f, 0.0f, 0.0f, 0.0f, 0.0f}, // v12
    };
    private int[] labels =
            {1, 2, 2, 3, 3,
                    4, 4, 5, 5, 6,
                        7, 7, 7};

    private int userNum = 13;

    @Override
    public List<Link> getAvailableNeighbors(int userId, float weight) {
        ArrayList<Link> res = new ArrayList<>();
        return res;
    }

    @Override
    public List<Link> getAvailableNeighbors(int userId, float weight, int label) {
        List<Link> res = new ArrayList<>();
        for(int i=0; i<userNum; i++) {
                if(graph[userId][i]>0.01 && labels[i] == label && graph[userId][i] <=weight) {
                    res.add(new Link(i, label, graph[userId][i]));
            }
        }
        return res;
    }

    @Override
    public ArrayList<Integer> getUserWithLabel(int label) {
        ArrayList<Integer> res = new ArrayList<>();
        for(int i=0; i<labels.length; i++) {
            if(labels[i] == label) {
                res.add(i);
            }
        }
        return res;
    }

    @Override
    public float getMinDistance(int fromUserId, int toUserId, float maxDistance) {
        return graph[fromUserId][toUserId];
    }

    @Override
    public List<Link> findNeighbors(int userId) {
        ArrayList<Link> res = new ArrayList<>();
        for(int i=0; i<labels.length; i++) {
            if(graph[userId][i] != 0) {
                Link link = new Link(i,labels[i],graph[userId][i]);
                res.add(link);
            }
        }
        return res;
    }

    @Override
    public float getDirectWeight(int fromUserId, int toUserId) {
        return graph[fromUserId][toUserId];
    }

    @Override
    public int getLabel(int userId) {
        return labels[userId];
    }

    @Override
    public int[][] toAdjMatrix() {
        return new int[0][];
    }

    @Override
    public int[] getLabelsArr() {
        return labels;
    }

    @Override
    public int getNodeNumber() {
        return graph.length;
    }

    @Override
	public void addEdge(int node1, int node2, int weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWeight(int node1, int node2, int targat_weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void del_label(List<Integer> userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMinDistance(int fromUserId, int toUserId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
