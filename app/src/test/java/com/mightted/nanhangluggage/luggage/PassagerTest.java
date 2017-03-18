package com.mightted.nanhangluggage.luggage;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.litepal.crud.DataSupport;

import java.util.List;

import static android.R.attr.id;
import static org.junit.Assert.*;

/**
 * Created by 晓深 on 2017/3/16.
 */
public class PassagerTest {

    private Passager passager;


    @Before
    public void setUp() throws Exception {
        passager = new Passager(8,"成人、儿童、占座婴儿","公务舱");

    }

    @After
    public void tearDown() throws Exception {

    }

    @Ignore
    public void testLuggage() {

    }

    @Test
    //根据行李具体情况，结合相关航班区域的行李托运超额行李处理机制进行测试
    public void testCost() {
//        List<Luggage> luggages = DataSupport.where("type = ? and areaType = ?","公务舱","2").find(Luggage.class);
//        Luggage mLuggage = luggages.get(0);
//        Luggage mLuggage = new Luggage(6,2,"公务舱",3,32,158);
//        Package mPackage = new Package(46,20,20,20);
//        assertEquals(1000, passager.getCost(mPackage, mLuggage, true,false));

        assertEquals(0, passager.getCost(new Package(31,20,20,20), new Luggage(6,2,"公务舱",3,32,158), false,false)); //正常情况下行李的情况
        assertEquals(3000, passager.getCost(new Package(42,20,20,20), new Luggage(6,2,"公务舱",3,32,158), false,false));  //普通的超重
        assertEquals(-1, passager.getCost(new Package(46,20,20,20), new Luggage(8,2,"公务舱",3,32,158), false,false));  //严重超重，不允许带上飞机
        assertEquals(-1, passager.getCost(new Package(31,20,120,20), new Luggage(6,2,"公务舱",3,32,158), false,false));  //超尺寸
        assertEquals(1000, passager.getCost(new Package(31,20,20,20), new Luggage(6,2,"公务舱",3,32,158), true,true));  //第一次超件，行李未超重
        assertEquals(3000, passager.getCost(new Package(43,20,20,20), new Luggage(6,2,"公务舱",3,32,158), true,true));  //第一次超件，行李超重
        assertEquals(-1, passager.getCost(new Package(23,20,120,20), new Luggage(6,2,"公务舱",3,32,158), true,true));  //第一次超件，行李超尺寸
        assertEquals(-1, passager.getCost(new Package(43,20,120,20), new Luggage(6,2,"公务舱",3,32,158), true,true));  //第一次超件，行李超尺寸，超重
        assertEquals(2000, passager.getCost(new Package(31,20,20,20), new Luggage(6,2,"公务舱",3,32,158), true,false));  //第二次超件，行李未超重
        assertEquals(3000, passager.getCost(new Package(43,20,20,20), new Luggage(6,2,"公务舱",3,32,158), true,false));  //第二次超件，行李超重
        assertEquals(-1, passager.getCost(new Package(23,20,120,20), new Luggage(6,2,"公务舱",3,32,158), true,false));  //第二次超件，行李超尺寸
        assertEquals(-1, passager.getCost(new Package(43,20,120,20), new Luggage(6,2,"公务舱",3,32,158), true,false));  //第二次超件，行李超尺寸，超重

    }

    @Test
    public void testAllCost() {
        //超重，普通，普通，第一次普通超件，第二次普通超件，第三次超重超件
        Package[] p ={new Package(42,20,20,20),new Package(31,20,20,20),new Package(31,20,20,20),new Package(31,20,20,20),new Package(31,20,20,20),new Package(34,20,20,20)};
        passager.setPackage(p);
        assertEquals(false,passager.checkAllCosts());
        System.out.println(passager.showCost());
    }

}