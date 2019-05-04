package cn.neu.kou.teambuild.steiner;

import java.util.*;

/**
 * Created by eaglesky on 5/24/16.
 */
public class FastSteinerTree {
    private int[][] inputGraph;//输入的图邻接矩阵
    private int[] inputSteinerPoints; //输入的斯坦纳节点数组

    private int[][] steinerPointsGraph;  //斯坦纳节点图
    private int[][] steinerTree;         //斯坦纳树
    private HashMap<Edge, List<Edge>> edgeMap;  //哈希影射？？？？？

    public static final int NOT_LINK = -1;    //无连接的标记为-1

    public static int[][] prim(int[][] graph) {
        int[][] mst = new int[graph.length][graph.length];
        initGraph(mst);
        HashSet<Integer> wholeSet = new HashSet<Integer>();
        HashSet<Integer> vSet = new HashSet<Integer>();

        int start = 0;
        for (int i = 0; i < graph.length; i++) {
            for (int j = i + 1; j < graph.length; j++) {
                if (graph[i][j] != NOT_LINK) {
                    wholeSet.add(i);
                    wholeSet.add(j);
                    start = i;
                }
            }
        }

        vSet.add(start);//加入开始节点v
        while (vSet.size() < wholeSet.size()) {
            int minWeight = Integer.MAX_VALUE;
            Edge mwe = new Edge(-1, -1);
            for (int i = 0; i < graph.length; i++) {
                for (int j = i + 1; j < graph.length; j++) {
                    if (mst[i][j] == NOT_LINK && graph[i][j] != NOT_LINK) {
                        boolean iInJOut = vSet.contains(i) && !vSet.contains(j);
                        boolean iOutJIn = !vSet.contains(i) && vSet.contains(j);
                        boolean lessWeight = graph[i][j] < minWeight;
                        if ((iInJOut || iOutJIn) && lessWeight) {
                            minWeight = graph[i][j];
                            mwe.setNode1(i);
                            mwe.setNode2(j);
                        }
                    }
                }
            }
            if (minWeight != Integer.MAX_VALUE) {
                vSet.add(mwe.getNode1());
                vSet.add(mwe.getNode2());
                mst[mwe.getNode1()][mwe.getNode2()] = mst[mwe.getNode2()][mwe.getNode1()] = minWeight;
                //System.out.printf("[Prim] Add edge (%d, %d)\n", mwe.getNode1(), mwe.getNode2());
            }
        }
        return mst;
    }

    /**
     * 路径和
     * @param graph   图数据
     * @param nodes   节点数据
     * @return        路径和
     */
    public static int sumDistance(int[][] graph, int[] nodes) {
        int sum = 0;
        ShortestPathWithDist shortestPathWithDist;

        for(int i=0; i<nodes.length; i++) {
            for(int j=0; j<nodes.length; j++) {
                if(i != j) {
                    shortestPathWithDist = dijkstra(graph, nodes[i], nodes[j]);
                    sum += shortestPathWithDist.distance;
                }
            }
        }
        return sum;
    }

    /**
     * 图的最小直径
     * @param graph   图数据
     * @param nodes   节点数据
     * @return        最小直径
     */
    public static int minDiameter(int[][] graph, int[] nodes) {
        int minDia = 0;
        int curDia = 0;
        ShortestPathWithDist shortestPathWithDist;

        for(int i=0; i<nodes.length; i++) {
            for(int j=0; j<nodes.length; j++) {
                if(i != j) {
                    shortestPathWithDist = dijkstra(graph, nodes[i], nodes[j]);
                    curDia = shortestPathWithDist.distance;
                    if(curDia > minDia) {
                        minDia = curDia;
                    }
                }
            }
        }

        return minDia;
    }

    /**
     * 最小生成树距离
     * @param graph   原始图
     * @param nodes   节点
     * @return
     */
    public static int minBuildTree(int[][] graph, int[] nodes) {
        FastSteinerTree fst = new FastSteinerTree(graph, nodes);
        int[][] st = fst.execute();

        int sum = 0;
        for (int i = 0; i < st.length; i++) {
            for (int j = i + 1; j < st.length; j++) {
                if (st[i][j] != NOT_LINK) {//有边
                    sum += st[i][j];
                }
            }
        }

        return sum;
    }

    /**
     * 基于结构约束的评价标准
     * @param graph   原始图
     * @param fromList  起点数组
     * @param toList    终点数组
     * @return         代价
     */
    public static int minStructRestrict(int[][] graph, List<Integer> fromList, List<Integer> toList) {
        int sum = 0;
        for(int i=0; i<fromList.size(); i++) {
            sum += dijkstra(graph, fromList.get(i), toList.get(i)).distance;
        }
        return sum;
    }

    public static ShortestPathWithDist dijkstra(int[][] graph, int source, int target) {
        int[] d = new int[graph.length];
        int[] previous = new int[graph.length];
        HashSet<Integer> nodes = new HashSet<Integer>();
        for (int i = 0; i < graph.length; i++) {
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
            if (u == target) {
                break;
            }
            for (int i = 0; i < graph.length; i++)
            {
                if (graph[u][i] != NOT_LINK && d[i] > d[u] + graph[u][i]) {
                    d[i] = d[u] + graph[u][i];
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

    public static void initGraph(int[][] graph) { //初始化图的邻接矩阵为全-1
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                graph[i][j] = NOT_LINK;
            }
        }
    }

    public static void addEdge(int[][] graph, Edge e, int dist) {  //加入边对应的距离
        graph[e.getNode1()][e.getNode2()] = graph[e.getNode2()][e.getNode1()] = dist;
    }

    public FastSteinerTree(int[][] iGraph, int[] iSteinerPoints) {  //输入图邻接矩阵和斯坦纳节点数组
        inputGraph = iGraph;
        inputSteinerPoints = iSteinerPoints;
    }

    public int[][] execute() {//返回斯坦纳树的矩阵表示
        constructSteinerPointsGraph();
        steinerPointsGraph = prim(steinerPointsGraph);
        constructSubGraph();
        //printGraphEdges(steinerTree);

        steinerTree = prim(steinerTree);
        removeNonSteinerLeaves();
        //printGraphEdges(steinerTree);

        return steinerTree;
    }

    public void constructSteinerPointsGraph() {  //构建斯坦纳节点图edgeMap:二维链表
        steinerPointsGraph = new int[inputSteinerPoints.length][inputSteinerPoints.length];
        initGraph(steinerPointsGraph);
        edgeMap = new HashMap<Edge, List<Edge>>();
        for (int i = 0; i < inputSteinerPoints.length; i++) {
            for (int j = i + 1; j < inputSteinerPoints.length; j++) {
                ShortestPathWithDist spwd = dijkstra(inputGraph, inputSteinerPoints[i], inputSteinerPoints[j]);
                steinerPointsGraph[i][j] = steinerPointsGraph[j][i] = spwd.distance;
                edgeMap.put(new Edge(i, j), spwd.shortestPath);
            }
        }
    }

    public void constructSubGraph() {//构建子图
        steinerTree = new int[inputGraph.length][inputGraph.length];//声明斯坦纳树
        initGraph(steinerTree); //初始化斯坦纳树为-1
        for (int i = 0; i < steinerPointsGraph.length; i++) {//排序
            for (int j = i + 1; j < steinerPointsGraph.length; j++) {
                if (steinerPointsGraph[i][j] != -1) {
                    List<Edge> shortestPath = edgeMap.get(new Edge(i, j));//创建最短路径列表：保存edgeMap中存储信息
                    if (shortestPath == null) {//无最短路径，输出找不到信息
                        System.out.println("[Error] Can't find in edgeMap");
                    }
                    for (Edge e : shortestPath) {  //边在最短路径中，ei,ej为边的两个节点
                        int ei = e.getNode1();
                        int ej = e.getNode2();
                        steinerTree[ei][ej] = steinerTree[ej][ei] = inputGraph[ei][ej];//保存图矩阵中对应信息到子图中
                    }
                }
            }
        }
    }

    public void removeNonSteinerLeaves() {
        int[] removeNodes = new int[steinerTree.length];
        for (int id : inputSteinerPoints) {
            removeNodes[id] = 1;
        }
        int cleanCount;
        do {
            cleanCount = 0;
            for (int i = 0; i < steinerTree.length; i++) {
                if (removeNodes[i] == 0) {
                    int degree = 0;
                    int j;
                    for (j = 0; j < steinerTree.length; j++) {
                        if (steinerTree[i][j] != NOT_LINK) {
                            degree++;
                        }
                    }
                    if (degree == 1) {
                        removeNodes[i] = -1;
                        steinerTree[i][j] = steinerTree[j][i] = NOT_LINK;
                        cleanCount++;
                    }
                }
            }
        } while (cleanCount > 0);
    }

    public static void printGraphEdges(int[][] graph) { //打印图的边信息：左右节点
        int edgeCount = 0;
        for (int i = 0; i < graph.length; i++) {
            for (int j = i + 1; j < graph.length; j++) {
                if (graph[i][j] != NOT_LINK) {//有边
                    System.out.printf("(%d, %d) ", i, j);
                    edgeCount++;
                }
            }
        }
        if (edgeCount == 0) {
            System.out.println("Empty graph!");
        } else {
            System.out.println();
        }
    }

    public static void main(String[] args) {//主函数
        int[][] graph = new int[9][9];
        initGraph(graph);//初始化图
        addEdge(graph, new Edge(0, 1), 20);
        addEdge(graph, new Edge(0, 8), 2);
        addEdge(graph, new Edge(7, 8), 1);
        addEdge(graph, new Edge(6, 7), 1);
        addEdge(graph, new Edge(5, 6), 2);
        addEdge(graph, new Edge(4, 5), 2);
        addEdge(graph, new Edge(4, 8), 2);
        addEdge(graph, new Edge(1, 5), 2);
        addEdge(graph, new Edge(1, 2), 16);
        addEdge(graph, new Edge(2, 4), 4);
        addEdge(graph, new Edge(2, 3), 18);
        addEdge(graph, new Edge(3, 4), 4);

        System.out.println("Input Graph:");
        printGraphEdges(graph);

        FastSteinerTree fst = new FastSteinerTree(graph, new int[]{0 ,1, 2, 3});
        int[][] st = fst.execute();

        System.out.println("Steiner Tree:");
        printGraphEdges(st);
    }

}
