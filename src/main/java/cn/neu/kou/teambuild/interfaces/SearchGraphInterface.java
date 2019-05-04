package cn.neu.kou.teambuild.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public interface SearchGraphInterface extends GraphInterface {
    /**
     * 返回当前搜索子图的查询序列
     * @return   根据查询序列得到的用户id序列
     */

    List<Integer> getSearchSequence();
    /**
     * 返回未经CFL分解的序列，即直接按节点ID大小返回
     * @return 根据查询序列得到的用户id序列
     */
    List<Integer> getSeq_noCFL();
    /**
     * 返回一个与节点序列对应的标签序列
     * @return已去重
     */
    List<Integer> getSeq_label();


    /**
     * 边的条数
     * @return
     */
    int getTotalEdge();
}
