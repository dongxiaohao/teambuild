package test.buildcpitree;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/30
 */
public class SearchGraphTest  implements SearchGraphInterface {

    private float[][] graph = {
            // u1 u2 u3 u4  u5 u6 u7
               {0, 3, 6, 0, 0, 0, 0}, //u1
               {3, 0, 4, 2, 4, 0, 0}, //u2
               {6, 4, 0, 0, 0, 1, 0}, //u3
               {0, 2, 0, 0, 0, 0, 0}, //u4
               {0, 4, 0, 0, 0, 0, 3}, //u5
               {0, 0, 1, 0, 0, 0, 0}, //u6
               {0, 0, 0, 0, 3, 0, 0}, //u7
    };

    private int[] labels = {
            1, 2, 3, 4, 5, 6, 7
    };

    @Override
    public List<Integer> getSearchSequence() {
        ArrayList<Integer> res = new ArrayList<>();
        for(int i=0; i<7; i++) {
            res.add(i);
        }
        return  res;
    }

    @Override
    public ArrayList<Link> findNeighbors(int userId) {
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
        int[][] g = new int[graph.length][];
        for(int i=0; i<this.graph.length; i++) {
            g[i] = new int[this.graph.length];
            for(int j=0; j<this.graph.length; j++) {
                g[i][j] = (int) this.graph[i][j];
            }
        }
        return g;
    }

    @Override
    public List<Integer> getLabelsArr() {
        return labels;
    }

    @Override
    public int getNodeNumber() {
        return this.graph.length;
    }

    @Override
	public List<Integer> getSeq_noCFL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getSeq_label() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public int getTotalEdge() {
        int sum = 0;
        for(int i=0; i<graph.length; i++) {
            for(int j=i+1; j<graph.length; j++) {
                if(graph[i][j] != 0) {
                    sum += 1;
                }
            }
        }

        return sum;
    }
}
