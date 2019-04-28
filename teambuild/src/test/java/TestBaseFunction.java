import cn.neu.kou.teambuild.cpi.CpiTree;
import cn.neu.kou.teambuild.cpi.Search;
import cn.neu.kou.teambuild.cpi.SearchGraphHelper;
import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.query_graph;
import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.OriginGraphInterface;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;
import cn.neu.kou.teambuild.main.Criteria;
import cn.neu.kou.teambuild.main.MinCostTeam;
import org.junit.Test;
import test.buildcpitree.OriginGraphTest;
import test.buildcpitree.SearchGraphTest;

import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public class TestBaseFunction {
    private OriginGraphInterface originGraph = new OriginGraphTest();
    private SearchGraphInterface searchGraph = new SearchGraphTest();
    private SearchGraphHelper searchGraphHelper = new SearchGraphHelper(searchGraph);
    private CpiTree cpiTree = new CpiTree(originGraph, searchGraph);

    @Test
    public void testBuildTree() {
        //测试结果，树的结构已经能够建立
        SearchGraphHelper searchGraphHelper = new SearchGraphHelper(searchGraph);
        System.out.println(searchGraphHelper.getCurrentUserId());
        System.out.println(searchGraphHelper.getCurrentUserNeighborNum());
        System.out.println(searchGraphHelper.getNeighborNum(1));
        System.out.println(searchGraphHelper.getCurrentLabel());
        System.out.println(searchGraphHelper.getLabel(1));
        cpiTree.buildTree();
        System.out.println(cpiTree);
        System.out.println(cpiTree);
    }

    @Test
    public void testGenerateTeam() {
        cpiTree.buildTree();
        cpiTree.splitTree();
        Search search = new Search(cpiTree, Integer.MAX_VALUE);
        cpiTree.splitTree();
        search.generateAllTeam();

        Criteria criteria = new Criteria(originGraph, searchGraph);
        MinCostTeam minCostTeam = new MinCostTeam(criteria);
        minCostTeam.minStructRestrictTeam(search.getValidateTeamSet());
        minCostTeam.printMinStructRestricTeamAllCriteria();
        System.out.println(12);
    }

    @Test
    public void testGetAvaliableNeighbors() {
        List<Link> list = this.originGraph.getAvailableNeighbors(1, 4, 3);
        for(Link link : list) {
            System.out.println(link.getToUserId() + "  " + link.getLabel() + "   " + link.getWeight());
        }
    }

    @Test
    public void testWithReallyData() throws Exception {

        Origin_graph origin_graph = new Origin_graph(36692, 20, null, null);
        origin_graph.readMatrix("C:\\Users\\LQ\\Desktop\\kou_exp\\TeamBuild\\src\\main\\resources\\Email-Enron.txt");
        query_graph query_graph = new query_graph(5, 2, null, null);
        query_graph.init_zhitu("C:\\Users\\LQ\\Desktop\\kou_exp\\TeamBuild\\src\\main\\resources\\query_graph\\small.txt");


        CpiTree cpiTree = new CpiTree(origin_graph, query_graph);
        cpiTree.buildTree();
        cpiTree.splitTree();
        Search search = new Search(cpiTree, Integer.MAX_VALUE);
        search.generateAllTeam();
        System.out.println(12);
    }
}
