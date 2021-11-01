package cn.knet.util;


import java.util.ArrayList;

import java.util.HashMap;

import java.util.Iterator;

import java.util.List;

import java.util.Map;

import java.util.Set;

/**

 *

 * @author chenairu

 *

 */

public class listGetKey {
    public static void main(String[] args) {
//List list = setTestValues();

//for (int i = 0; i < list.size(); i++) {
//System.out.println(list.get(i));

//}

        GetMapKeyValue();

    }

    public static void GetMapKeyValue() {
// 1测试用的list

        List listResult = setTestValues();

// 2listKey用于存放list中map对象的key值

        List listKey = new ArrayList();

// 3取出测试List中的第一条数据，

        Map mapResult = (Map)listResult.get(0);

// 4取出测试List中的第一条数据中对应的Map的键值(Key)



// listHead用于存放遍历出来的mapKeySet的值

        String listHead = "";

        String keyOfListMapKey = "";

        Set mapKeySet = mapResult.keySet();
        Iterator iteratorKey = mapKeySet.iterator();

        String listValue = "";
        mapResult.forEach((x,y) ->{
            listKey.add(x);
        });





















        while(iteratorKey.hasNext()){
            listHead = iteratorKey.next()+"";

            //listKey.add(listHead);

            System.out.print("=="+listHead+"=====");

        }

        System.out.println();

// 循环得到list中的值

        for (int i = 0; i < listResult.size(); i++) {
            mapResult = (Map)listResult.get(i);

// 获取listKey(listResult对应的Map的键值)中的值

            for (int j = 0; j < listKey.size(); j++) {
                keyOfListMapKey = listKey.get(j) +"";

                listValue = mapResult.get(keyOfListMapKey)+"";

                System.out.print("=="+listValue+"==");

            }

            System.out.println();

        }

    }

    /**

     * 设置测试数据

     */

    private static List setTestValues(){
        List list = new ArrayList();

        Map map1 = new HashMap();

        Map map2 = new HashMap();

        Map map3 = new HashMap();

        for(int i=0;i<2;i++){
// map 中放入key和value值

            map1.put("map1Key"+i,"map1Values"+i);

        }

        for(int i=0;i<2;i++){
            map2.put("map1Key"+i,"map2Values"+i);

        }

        for(int i=0;i<2;i++){
            map3.put("map1Key"+i,"map3Values"+i);

        }

        list.add(map1);

        list.add(map2);

        list.add(map3);

// list中的值

//{map1Key1=map1Values1, map1Key0=map1Values0}

//{map1Key1=map2Values1, map1Key0=map2Values0}

//{map1Key1=map3Values1, map1Key0=map3Values0}

        return list;

    }

}
