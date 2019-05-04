import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.Integer;
import java.util.*;

public class test {

    public static void main(String[] args) throws Exception {
        int[][] t=new int[5][5];
        List<Integer> seq=new ArrayList<>();
        String path="D:\\学习\\kou_exp\\query_graph\\small.txt";
        init_zhitu(t,path);
        for(int i=0;i<t.length;i++) {
            for(int j=0;j<t.length;j++)
                System.out.print(t[i][j]+" ");
            System.out.println();
        }

        CFL(t,seq);
        for(int i:seq)
            System.out.print(i+" ");

    }

    //初始化查询子图

    public static void init_zhitu(int[][] t,String path) throws Exception {
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
                t[row][colume]=Integer.valueOf(mem.trim()).intValue();	//存入数组
                colume++;		// 移动对应列
                l_flag+=2;	//移动至下一个数字
                r_flag+=2;	//移动至下一个空格

            }
            row++;			//行数增加

        }
        fis.close();

    }


    //CFL 分解
    public static void CFL(int[][] mat, List<Integer> sep) {
        LinkedHashSet<Integer> s=new LinkedHashSet<>();
        List<Integer> l=new ArrayList<>();
        int[][] n_m=new int[mat.length][mat.length];
        int flag=0;
        for(int i=0;i<mat.length;i++)
            for(int j=0;j<mat.length;j++)
                n_m[i][j]=mat[i][j];
        //筛选出叶子和森林节点 并加入链表l
        while(flag==0)
        {
            flag=1;
            for(int i=0;i<mat.length;i++) {
                int count=0;
                for(int j=0;j<mat.length;j++) {
                    if(n_m[i][j]!=0) count++;
                }
                if(count==1) {
                    flag=0;
                    l.add(i);
                }
            }
            for(int i:l)
                for(int m=0;m<mat.length;m++) {
                    n_m[i][m]=0;
                    n_m[m][i]=0;
                }
        }
        //将第一个核心节点加入集合 添加度最小的类型节点
        s.add(4);
        //进行深度优先遍历,为防止相同节点存入 故使用set
        for(int a=0;a<s.size();a++) {
            String str=s.toString();			//将集合转化为字符串
            int mem=str.charAt(a*3+1)-'0';	//选找到对应位的节点编号
            //System.out.println(str+"   "+str.charAt(a*3+1)+"  "+mem);
            for(int i=0;i<n_m.length;i++) 	//对当前节点编号进行遍历
                if(n_m[i][mem]!=0) {
                    //  System.out.print(n_m[i][mem]+" ");
                    s.add(i);	//

                }
        }
        //加入叶子和森林节点
        for(int a:s)
            sep.add(a);
        for(int i=l.size()-1;i>=0;i--) {
            sep.add(l.get(i));
        }

    }


}
