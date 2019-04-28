package cn.neu.kou.teambuild.util;

import java.util.HashSet;
import java.util.Set;

public class Util {
    /**
     * 将set中的元素拷贝到另外一个set中
     * @param fromSet    源set
     * @param toSet      目标set
     */
    public static void copyHashSet(Set<Integer> fromSet, HashSet<Integer> toSet) {
        toSet.clear();
        for(int i : fromSet) {
            toSet.add(i);
        }
    }

    /**
     * 拷贝集合
     * @param origin  源集合
     * @param target  目的集合
     */
    public static void copySet(Set<Integer> origin, Set<Integer> target) {
        for(int i : origin) {
            target.add(i);
        }
    }

}
