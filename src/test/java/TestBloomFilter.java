import cn.neu.kou.teambuild.util.BloomFilter;
import org.junit.Test;

import java.util.BitSet;

public class TestBloomFilter {

    @Test
    public void testBloomFilter() {
        /**
         * 测试结论：假阳率和bit的位数具有很大的关系，如果按照0.1 10000,10000个值的情况，大于需要57708bit,也就是说一个bit大约
         */
        long startTime = System.currentTimeMillis();
        BloomFilter<Integer> bloomFilter = new BloomFilter<Integer>(0.1, 20);
        for(int i=0; i<10000; i++) {
            bloomFilter.add(i);
        }

        int errorTime = 0;
        for(int i=0; i<100000; i++) {
            if(!bloomFilter.contains(i)) {
                ++errorTime;
            }
        }
        System.out.println("error times: " + errorTime);
        System.out.println("cost time: " + (System.currentTimeMillis()-startTime));
    }

    @Test
    public void testBitSetClone() {
        // test 结论，BitSet的拷贝是深拷贝
        BitSet bitset = new BitSet(10);
        bitset.set(1, true);
        BitSet bitSet2 = (BitSet) bitset.clone();
        System.out.println(bitSet2.get(1));
        System.out.println(bitSet2.get(0));
    }

    @Test
    public void testBloomBuildBasesdOld() {
        // 测试结果，基于已有BloomFilter的布隆过滤器创建新的布隆过滤器好使
        BloomFilter<Integer> bloomFilter = new BloomFilter<Integer>(0.1, 100);
        for(int i=0; i<90; i++) {
            bloomFilter.add(i);
        }
        BloomFilter<Integer> bloomFilter1 = new BloomFilter<Integer>(bloomFilter);
        int okNum = 0;
        for(int i=0; i<90; i++) {
            if(bloomFilter1.contains(i)) {
                okNum += 1;
            }
        }
        bloomFilter.add(100);
        System.out.println(bloomFilter.contains(100));
        System.out.println(bloomFilter1.contains(100));
        bloomFilter1.add(99);
        System.out.println(bloomFilter1.contains(99));
        System.out.println(bloomFilter.contains(99));
        System.out.println(okNum);
    }

    @Test
    public void testBloomFilter2() {
        BloomFilter bloomFilter = new BloomFilter(0.01, 10000);

        for(int i=0; i<90; i++) {
            bloomFilter.add(i);
        }

        int errorTimes = 0;
        for(int i=100; i<100000; i++) {
            if(bloomFilter.contains(i)) {
                errorTimes += 1;
            }
        }
        System.out.println(errorTimes);
    }
}
