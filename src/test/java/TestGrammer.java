import cn.neu.kou.teambuild.interfaces.Link;
import org.junit.Test;
import org.omg.PortableInterceptor.INACTIVE;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author : lq
 * @date : 2019/3/29
 */
public class TestGrammer {
    @Test
    public void testCreateZeroBranches() {
        // 测试结果表明，创建长度为0的ArrayList数组是合法的阿
        ArrayList<Integer> list = new ArrayList<>(0);
        ArrayList<Integer> list2 = new ArrayList<>(-1);
    }

    @Test
    public void testHashMap() {
        //测试结果为修改hashMap的KeySet，HashMap会受到影响，所以不能直接修改KeySet
        HashMap<Integer, String> hashMap = new HashMap<>();
        hashMap.put(1, "12321");
        hashMap.put(2, "sdsd");
        System.out.println(hashMap.keySet());
        System.out.println(hashMap.get(1));
    }

    @Test
    public void testSet() {
        //测试结果：对集合进行求交集操作会修改原来的结果
        HashSet<Integer> set1 = new HashSet<>();
        HashSet<Integer> set2 = new HashSet<>();
        set1.add(10);
        set1.add(11);
        set2.add(11);
        System.out.println(set1.retainAll(set2));
        System.out.println(set1);
        System.out.println(set2);
    }

    @Test
    public void testLink() {
        //测试结果表明，泛型的类型转化时每次只能转化一个级别，
        Link link = new Link(0 , 0, 1.2f);
        ArrayList<Link> list = new ArrayList<>();
        list.add(link);
        Link[] links = (Link[]) list.toArray();
        System.out.println(links[0]);
    }

    @Test
    public void testBox() {
        //测试结果，java中自动装箱和拆箱不影响使用
        Integer i = new Integer(123123);
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(i, 12);
        System.out.println(map.get(new Integer(123123)));
        map.put(12, 24);
        System.out.println(map.get(new Integer(12)));
        map.put(new Integer(123), 123);
        System.out.println(map.get(123));
    }


    @Test
    public void testFloat() {
        //测试结果，浮点数如果一旦涉及类型转化后本来相同的两个数会存在细微差别
        float f = 0.1f;
        System.out.println((double)f == 0.1);
        System.out.println((double)f < 0.1);
        System.out.println((double)f > 0.1);
        System.out.println(f);
        System.out.println((double)f);
    }
    @Test
    public void testList() {
    	System.out.println("----------");
    	ArrayList<Integer> li=new ArrayList<>();
    	int i=0;
    	while(i<5){
    		li.add(i+5);
    		i++;
    	}
    	System.out.println(li.indexOf(7)+" ");
    	li.remove(li.indexOf(7));
    	for(int j:li)
    		System.out.print(j);
    }

    @Test
    public void testArrayList() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(12321);
        Object[] res = list.toArray();
        res[0] = 1;
        Object[] res2 = list.toArray();
        res2[0] = 1;
        System.out.println(res == res2);
        System.out.println(res[0]);
        System.out.println(res[0]);
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        System.out.println(hashMap.keySet() == hashMap.keySet());
        List  ll =  Arrays.asList(res);
        ll.add(0);
    }

    @Test
    public void testAdd() {
        HashMap<Integer, Set<Link>> adjMap = new HashMap<>();  //邻接表
        HashSet<Link> set = new HashSet<>();
        adjMap.put(0, set);
        adjMap.get(0).remove(new Link(0, 0, 0));
    }
}
