package cn.neu.kou.teambuild.graph;

import cn.neu.kou.teambuild.interfaces.Link;
import cn.neu.kou.teambuild.interfaces.SearchGraphInterface;
import cn.neu.kou.teambuild.io.big_test01;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public class query_graph implements  SearchGraphInterface {
    public int[][] query_matrix;
    public ArrayList<Integer> seq;
    public lables query_lable;
    /**
     * 封装了init_zhitu方法
     * @param scale 节点数
     * @param count 标签数
     * @param query_path 查询图文件路径
     * @param label_path 标签文件路径
     * @throws Exception
     */
    public query_graph(int scale,int count,String query_path,String label_path) throws Exception {
        this.query_matrix=new int[scale][scale];
        this.seq=new ArrayList<>();
        init_zhitu(query_path);
        this.query_lable=new lables(scale,count,label_path);
    }

    //初始化查询子图
    public void init_zhitu(String path) throws Exception {
        File file = new File(path);
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件
        String line = "";
        int row=0,colume=0;
        while((line = reader.readLine()) != null) {
            colume=0;
            //设置初始值
            int l_flag=0,r_flag=1;
            line=line.trim();
            int len=line.length();
            while(r_flag<=len){
                String mem;;
                //分情况读取数字
                if(r_flag<len-1)
                    mem=line.substring(l_flag, r_flag);
                else
                    mem=line.substring(l_flag);
                this.query_matrix[row][colume]=Integer.valueOf(mem.trim()).intValue();	//存入数组
                colume++;		// 移动对应列
                l_flag+=2;	    //移动至下一个数字
                r_flag+=2;	    //移动至下一个空格

            }
            row++;			//行数增加

        }
        fis.close();

    }


    //CFL 分解
    public void CFL(Origin_graph graph){
        LinkedHashSet<Integer> s=new LinkedHashSet<>();
        List<Integer> l=new ArrayList<>();
        int scale=this.query_matrix.length;
        int[][] n_m=new int[scale][scale];
        int flag=0;
        for(int i=0;i<scale;i++)
            for(int j=0;j<scale;j++) {
                n_m[i][j]=this.query_matrix[i][j];
               // System.out.print(n_m[i][j]+"  ");
            }
       // System.out.println();
        //筛选出叶子和森林节点 并加入链表l
        while(flag==0)
        {
            flag=1;
            for(int i=0;i<scale;i++) {
                int count=0;
                for(int j=0;j<scale;j++) {
                    if(n_m[i][j]!=0) count++;
                }
                if(count==1) {
                    flag=0;
                    l.add(i);
                }
            }
            for(int i:l)
                for(int m=0;m<scale;m++) {
                    n_m[i][m]=0;
                    n_m[m][i]=0;
                }
        }
        //将第一个核心节点加入集合 添加度最小的类型节点
        int min_node=0,min_degree=Integer.MAX_VALUE;
        for(int i=0;i<scale;i++) {
            for(int j=0;j<scale;j++) {
                if(n_m[i][j]!=0) {												//寻找到度不为零的节点
                    int pre_lable=this.query_lable.getlable(i);					//得到当前节点的标签
                    if(graph.getlable_count(pre_lable)<min_degree) {		    //判断大图中对应类型的节点是否为当前最小
                        min_degree=graph.getlable_count(pre_lable);
                        min_node=i;
                    }
                    break;						//跳出当前循环
                }
            }
        }
        s.add(min_node); 			//将最小匹配节点加入集合 S
        //进行深度优先遍历,为防止相同节点存入 故使用set
        for(int a=0;a<s.size();a++) {
            String str=s.toString();			//将集合转化为字符串
            str=str.substring(1,str.length()-1);
            String[] stringArray=str.split(",");
            int mem=Integer.valueOf(stringArray[a].trim());	//选找到对应位的节点编号
            //System.out.println(str+"   "+mem+"  "+stringArray[a]);

            for(int i=0;i<n_m.length;i++) 	//对当前节点编号进行遍历
                if(n_m[i][mem]!=0) {
                     // System.out.print(n_m[i][mem]+" ");
                    s.add(i);	//

                }
        }
        //加入叶子和森林节点
        for(int a:s)
            this.seq.add(a);
        for(int i=l.size()-1;i>=0;i--) {
            this.seq.add(l.get(i));
        }

    }
    public void printseq() {
        for(int i:this.seq)
            System.out.print(i+" ");
        System.out.println();
    }
    public void printquery_matrix() {
        for(int i=0;i<this.query_matrix.length;i++) {
            for(int j=0;j<this.query_matrix.length;j++)
                System.out.print(this.query_matrix[i][j]+" ");
            System.out.println();
        }
    }

	@Override
	/*
	 * 返回与目标节点直接相连的的节点连接的链表集，包括ID，标签，权重
	 * (non-Javadoc)
	 * @see cn.neu.kou.teambuild.interfaces.GraphInterface#findNeighbors(int)
	 */
	public List<Link> findNeighbors(int userId) {
		ArrayList<Link> result=new ArrayList<Link>();
		for(int i=0;i<this.query_matrix.length;i++) {
			if(this.query_matrix[userId][i]!=0) {
				result.add(new Link(i,this.query_lable.getlable(i),this.query_matrix[userId][i]));
				//System.out.println(i+" "+this.query_lable.getlable(i)+" "+this.query_matrix[userId][i]);
			}
			
		}
		return result;

	}
	/*
	 *返回权重，如果两个节点不直接相连则返回零
	 * @see cn.neu.kou.teambuild.interfaces.GraphInterface#getDirectWeight(int, int)
	 */
	@Override
	public float getDirectWeight(int fromUserId, int toUserId) {
		// TODO Auto-generated method stub
		return this.query_matrix[fromUserId][toUserId];
	}
	/*
	 * 返回当前节点的标签
	 * @see cn.neu.kou.teambuild.interfaces.GraphInterface#getLabel(int)
	 */
	@Override
	public int getLabel(int userId) {
		// TODO Auto-generated method stub
		return this.query_lable.getlable(userId);
	}
	/*
	 * 返回查询序列
	 * @see cn.neu.kou.teambuild.interfaces.SearchGraphInterface#getSearchSequence()
	 */
	@Override
	public ArrayList<Integer> getSearchSequence() {
		// TODO Auto-generated method stub
		return this.seq;
	}

	@Override
	/*
	 * 返回未经CFL分解的序列，即直接按节点ID大小返回
	 * @see cn.neu.kou.teambuild.interfaces.SearchGraphInterface#getSeq_noCFL()
	 */
	public List<Integer> getSeq_noCFL() {
		// TODO Auto-generated method stub
		int size=this.query_matrix.length;
		List<Integer> result=new ArrayList<>();
		for(int i=0;i<size;i++)
			result.add(i);
		return result;
	}

	@Override
	/**
	 * 
	 */
	public List<Integer> getSeq_label() {
		// TODO Auto-generated method stub
		List<Integer> result=new ArrayList<>();
		for(int i:this.seq) {
			//if(!result.contains(this.getLabel(i)))		//如果result不包含i节点的标签，则添加标签
				result.add(this.getLabel(i));			//添加标签
		}
		return result;
	}
	/**
	 * 判断两个节点间是否有边
	 * @param fromnode
	 * @param tonode
	 * @return
	 */
	public boolean have_edge(int fromnode,int tonode) {
		if(this.query_matrix[fromnode][tonode]==0)
			return false;
		else {
			return true;
		}
	}
	/**
	 * 返回两个节点之间的权重（直接相连
	 * @param fromnode
	 * @param tonode
	 * @return
	 */
	public int  get_weigth(int fromnode,int tonode) {
		return this.query_matrix[fromnode][tonode];
		
	}
	public int get_seqLength(){
		return this.query_matrix.length;
		
	}


    @Override
    public int[][] toAdjMatrix() {
        return this.query_matrix;
    }

    @Override
    public List<Integer> getLabelsArr() {
        return this.query_lable.getLable();
    }

    @Override
    public int getNodeNumber() {
        return this.query_matrix.length;
    }

    @Override
    public int getTotalEdge() {
        int sum = 0;
        for(int i=0; i<query_matrix.length; i++) {
            for(int j=i+1; j<query_matrix.length; j++) {
                if(query_matrix[i][j] != 0) {
                    sum += 1;
                }
            }
        }

        return sum;
    }
}
