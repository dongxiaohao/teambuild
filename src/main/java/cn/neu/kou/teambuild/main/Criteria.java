package cn.neu.kou.teambuild.main;

import cn.neu.kou.teambuild.cpi.ValidateTeam;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;
import cn.neu.kou.teambuild.steiner.Edge;
import cn.neu.kou.teambuild.steiner.FastSteinerTree;
import cn.neu.kou.teambuild.steiner.ShortestPathWithDist;

import java.util.*;

public class Criteria {
    private OriginGraphInterface originGraph;
    private List<Integer> originGraphLabel;

    private int[][] searchGraph;
    private List<Integer> searchGaaphLabel;

    public Criteria(OriginGraphInterface originGraphInterface, SearchGraphInterface searchGraphInterface) {
        originGraph = originGraphInterface;
        originGraphLabel = originGraphInterface.getLabelsArr();

        searchGraph = searchGraphInterface.toAdjMatrix();
        searchGaaphLabel = searchGraphInterface.getLabelsArr();
    }

    /**
     * 得到搜索图里面的每条边
     * @return  搜索图里面的每条边数组
     */
    public int[][] getSearchGraphEdge() {
        ArrayList<Integer> fromList = new ArrayList<>();
        ArrayList<Integer> toList = new ArrayList<>();

        for(int i=0; i<searchGraph.length; i++) {
            for(int j=i+1; j<searchGraph.length; j++) {
                if(searchGraph[i][j] > 0) {
                    fromList.add(i);
                    toList.add(j);
                }
            }
        }

        int[][] res = new int[fromList.size()][];
        for(int i=0; i<fromList.size(); i++) {
            res[i] = new int[2];
            res[i][0] = fromList.get(i);
            res[i][1] = toList.get(i);
        }
        return res;
    }

    /**
     * 最小直径
     * @param nodes  查询节点集合
     * @return       最小结构和
     */
    public int minDiameter(int[] nodes) {
        int minDia = 0;
        int curDia = 0;
        ShortestPathWithDist shortestPathWithDist;

        for(int i=0; i<nodes.length; i++) {
            for(int j=0; j<nodes.length; j++) {
                if(i != j) {
                    shortestPathWithDist = dijkstra(nodes[i], nodes[j]);
                    curDia = shortestPathWithDist.distance;
                    if(curDia > minDia && curDia != Integer.MAX_VALUE) {
                        minDia = curDia;
                    }
                }
            }
        }

        return minDia;
    }

    /**
     * 最小生成树距离
     * @param nodes  查询节点集合
     * @return       最小结构和
     */
    public int minBuildTree(int[][] graph) {
    	/*
        FastSteinerTree fst = new FastSteinerTree(null, nodes);
        int[][] st = fst.execute();
        int temp;

        int sum = 0;
        for (int i = 0; i < st.length; i++) {
            for (int j = i + 1; j < st.length; j++) {
                temp = (int) originGraph.getDirectWeight(i, j);
                if (temp != 0) {//有边
                    sum += temp;
                }
            }
        }
        */
    	int n=searchGraph.length;
    	int start=0;
		int [][] mins=new int [n][2];//用于保存集合U到V-U之间的最小边和它的值，mins[i][0]值表示到该节点i边的起始节点
        //值为-1表示没有到它的起始点，mins[i][1]值表示到该边的最小值，
        //mins[i][1]=0表示该节点已将在集合U中
		int sum=0;
		for(int i=0;i<n;i++){//初始化mins
			
			if(i==start){
				mins[i][0]=-1;
				mins[i][1]=0;
			}else if( graph[start][i]!=-1){//说明存在（start，i）的边
				mins[i][0]=start;
				mins[i][1]= graph[start][i];
			}else{
				mins[i][0]=-1;
				mins[i][1]=Integer.MAX_VALUE;
			}
			//System.out.println("mins["+i+"][0]="+mins[i][0]+"||mins["+i+"][1]="+mins[i][1]);
		}
		for(int i=0;i<n-1;i++){
			int minV=-1,minW=Integer.MAX_VALUE;
			for(int j=0;j<n;j++){//找到mins中最小值，使用O(n^2)时间
			
				if(mins[j][1]!=0&&minW>mins[j][1]){
					minW=mins[j][1];
					minV=j;
				}
			}
			//System.out.println("minV="+minV);
			mins[minV][1]=0;
			//System.out.println("最小生成树的第"+i+"条最小边=<"+(mins[minV][0]+1)+","+(minV+1)+">，权重="+minW);
			sum+=minW;
			for(int j=0;j<n;j++){//更新mins数组
				if(mins[j][1]!=0){
					//System.out.println("MINV="+minV+"||tree[minV][j]="+tree[minV][j]);
						if( graph[minV][j]!=-1&& graph[minV][j]<mins[j][1]){
							mins[j][0]=minV;
							mins[j][1]= graph[minV][j];
						}
				}
			}
		}
			//System.out.println(sum);

        return sum;
    }

    /**
     * 最小结构和
     * @param nodes  查询节点集合
     * @return       最小结构和
     */
    public int minStructSum(int[] nodes) {
        int sum = 0;
        ShortestPathWithDist shortestPathWithDist;

        for(int i=0; i<nodes.length; i++) {
            for(int j=i+1; j<nodes.length; j++) {
                if(i != j) {
                    shortestPathWithDist = dijkstra(nodes[i], nodes[j]);
                    sum += shortestPathWithDist.distance;
                }
            }
        }
        return sum;
    }

    /**
     * 基于结构约束的代码
     * @param roleIdUserIdMap   候选角色与用户映射
     * @return                  代价
     */
    public int minStructRestrict(HashMap<Integer, Integer> roleIdUserIdMap) {
        ArrayList<Integer> fromList = new ArrayList<>();
        ArrayList<Integer> toList = new ArrayList<>();

        //默认边有序，从小节点指向大节点
        for(int roleId : roleIdUserIdMap.keySet()) {
            for (int i = roleId+1; i < searchGraph.length; i++) {
                if(searchGraph[roleId][i] > 0) {
                    fromList.add(roleIdUserIdMap.get(roleId));
                    toList.add(roleIdUserIdMap.get(i));
                }
            }
        }

        int sum = 0;
        for(int i=0; i<fromList.size(); i++) {
            sum += dijkstra(fromList.get(i), toList.get(i)).distance;
        }
        return sum;
    }
    /**
     * 根据生成序列构建矩阵
     * @param roleIdUserIdMap  候选角色与用户映射
     * @return	构建的矩阵
     */
    public int[][] build_matrix(HashMap<Integer, Integer> roleIdUserIdMap){
    	int[][] result=new int[searchGraph.length][searchGraph.length];
    	//默认边有序，从小节点指向大节点
        for(int roleId : roleIdUserIdMap.keySet()) {
            for (int i = roleId+1; i < searchGraph.length; i++) {
                if(searchGraph[roleId][i] > 0) {
                	//int dis=dijkstra(roleId, i).distance;
                	int dis=(int)originGraph.getMinDistance(roleId, i);
                   result[roleId][i]=dis;
                   result[i][roleId]=dis;
                }else {
                	{
                		result[roleId][i]=-1;
                        result[i][roleId]=-1;
                	}
				}
            }
        }
        return result;
    }

    /**
     * 迪杰斯特拉最短距离
     * @param source
     * @param target
     * @return
     */
    public ShortestPathWithDist dijkstra(int source, int target) {
        int[] d = new int[originGraph.getNodeNumber()];
        int[] previous = new int[originGraph.getNodeNumber()];
        int temp;

        HashSet<Integer> nodes = new HashSet<Integer>();
        for (int i = 0; i < originGraph.getNodeNumber(); i++) {
            d[i] = Integer.MAX_VALUE;
            previous[i] = -1;
            nodes.add(i);
        }
        d[source] = 0;
        while (!nodes.isEmpty()) {
            int u = -1;
            int min_dist = Integer.MAX_VALUE;
            for (int node : nodes) {
                if (d[node] < min_dist) {
                    min_dist = d[node];
                    u = node;
                }
            }
            nodes.remove(u);
            if (u == target || u == -1) {
                break;
            }
            for (int i = 0; i < originGraph.getNodeNumber(); i++)
            {
                    temp = (int) originGraph.getDirectWeight(u, i);
                    if (temp != 0 && d[i] > d[u] + temp) {
                        d[i] = d[u] + temp;
                        previous[i] = u;
                    }
            }
        }
        ArrayList<Edge> shortestPath = new ArrayList<Edge>();
        int u = target;
        while (previous[u] != -1) {
            shortestPath.add(new Edge(previous[u], u));
            u = previous[u];
        }
        Collections.reverse(shortestPath);
        return new ShortestPathWithDist(shortestPath, d[target]);
    }
}
