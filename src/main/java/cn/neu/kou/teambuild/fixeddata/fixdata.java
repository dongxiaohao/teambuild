package cn.neu.kou.teambuild.fixeddata;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.neu.kou.teambuild.graph.Edge;
import cn.neu.kou.teambuild.graph.Origin_graph;
import cn.neu.kou.teambuild.graph.lables;

public class fixdata {
    public static void main(String args[]) throws Exception{
 	   Edge[] graph = new Edge[36692];
    	List<Integer> lable =new ArrayList<Integer>();
    	for(int i=0;i<4039;i++) {
    		//Integer[] a=new Integer[1];
    		//a[0]=(int)(Math.random()*8);
    		lable.add((int)(Math.random()*20));
    	}
    	String path = "D:\\学习\\kou_exp\\数据集\\已下载数据\\facebook_combined.txt\\facebook_combined.txt";
    	
    	String small_path="D:\\\\学习\\\\kou_exp\\\\数据集\\\\已下载数据\\\\facebook_combined.txt";
    	String small_filename="new_facebook_combined";
    	readMatrix(path, graph);
    	fix_weight(graph, small_path, small_filename);
    	//createTxtFile(lable, small_path, small_filename);
    	//output_lable(lable, small_path, small_filename);
    	
    }
    
	 /**
     * 生成.TXT格式文件,行数几乎无上限
     */
    public static boolean createTxtFile(List<Integer[]> lable, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;

        try {
            // 含文件名的全路径
            String fullPath = filePath + File.separator + fileName + ".txt";

            File file = new File(fullPath);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file = new File(fullPath);
            file.createNewFile();

            // 格式化浮点数据
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMaximumFractionDigits(10); // 设置最大小数位为10

            // 格式化日期数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            // 遍历输出每行
            PrintWriter pfp = new PrintWriter(file, "UTF-8"); //设置输出文件的编码为utf-8
            for (Object[] rowData : lable) {
                StringBuffer thisLine = new StringBuffer("");
                for (int i = 0; i < rowData.length; i++) {
                    Object obj = rowData[i]; // 当前字段

                    // 格式化数据
                    String field = "";
                    if (null != obj) {
                        if (obj.getClass() == String.class) { // 如果是字符串
                            field = (String) obj;
                        } else if (obj.getClass() == Double.class || obj.getClass() == Float.class) { // 如果是浮点型
                            field = formatter.format(obj); // 格式化浮点数,使浮点数不以科学计数法输出
                        } else if (obj.getClass() == Integer.class || obj.getClass() == Long.class
                                || obj.getClass() == Short.class || obj.getClass() == Byte.class) { // 如果是整形
                            field += obj;
                        } else if (obj.getClass() == Date.class) { // 如果是日期类型
                            field = sdf.format(obj);
                        }
                    } else {
                        field = " "; // null时给一个空格占位
                    }

                    // 拼接所有字段为一行数据，用tab键分隔
                    if (i < rowData.length - 1) { // 不是最后一个元素
                        thisLine.append(field).append("\t");
                    } else { // 是最后一个元素
                        thisLine.append(field);
                    }
                }
                pfp.print(thisLine.toString() + "\n");
            }
            pfp.close();

        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
    
    public static void output_lable(List<Integer> list,String filePath,String fileName) {
    	
		FileWriter fileWriter = null;
		try {
			// 含文件名的全路径
            String fullPath = filePath + File.separator + fileName + ".txt";

            File file = new File(fullPath);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            
			fileWriter = new FileWriter(fullPath);//创建文本文件
			int count=0;
			//while(list!=null && list.next()){//循环写入
			for(int i:list) {
				fileWriter.write(i+"\r\n");//写入 \r\n换行
				count++;
			}
			//}
			//fileWriter.write("共"+count+"条");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public static void fix_weight(Edge[] graph,String filePath,String fileName){
    	FileWriter fileWriter = null;
		try {
			// 含文件名的全路径
            String fullPath = filePath + File.separator + fileName + ".txt";

            File file = new File(fullPath);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            
			fileWriter = new FileWriter(fullPath);//创建文本文件
			int count=0;
			for (int i=0;i<graph.length;i++) {
				Edge memEdge=graph[i];
				while (memEdge!=null) {
					String out=i+" "+memEdge.getadjvex()+" "+memEdge.getweight();
					fileWriter.write(out+"\r\n");//写入 \r\n换行
					memEdge=memEdge.getnext();
					count++;
				}
			}
			System.out.println(count);
			
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public static void readMatrix(String path,Edge[] graph) throws Exception {
        File file = new File(path);
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件

        String line = "";
        while((line = reader.readLine()) != null){
            //将输入分割成图中对应两个边
            line=line.trim();
            if(line.charAt(0)<='9'&&line.charAt(0)>='0') {
            String[] mem=line.split(" ");
            
            int e1= Integer.valueOf(mem[0].trim()).intValue(); //将第一条边转化为整数
            int e2= Integer.valueOf(mem[1].trim()).intValue(); //将第二调表转化为整数
            int weigth=(int)(Math.random()*3+1);      //随机生成权重 范围从一到3
            
            if(e1<e2) { //若e1<e2则表示还未添加，否则应已添加进邻接图中
                graph[e1]=new Edge(e2,weigth,graph[e1]); //将第二条边加入的一条边的邻接矩阵
                graph[e2]=new Edge(e1,weigth,graph[e2]); //将第一条边加入的二条边的邻接矩阵
            }
            System.out.println(line+":"+e1+"  ---  "+e2+" "+weigth);
            }
        }

        fis.close();

    }



}
