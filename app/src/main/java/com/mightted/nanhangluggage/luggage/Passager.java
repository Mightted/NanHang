package com.mightted.nanhangluggage.luggage;

import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.media.CamcorderProfile.get;

/**
 * Created by 晓深 on 2017/3/15.
 */

public class Passager {
    private int flightType;  //航班类型
    private String passengerType;  //乘客类型，这里有成人和婴儿
    private String spaceType;  //舱位类型
    private Package[] packages;  //乘客的行李
    private int cost;
    private int maxWeight;  //航班允许的最大重量
    private int maxLength;  //航班允许的最大长度和
    List<Luggage> luggages;

    public Passager(int flightType, String passagerType, String spaceType) {
        this.flightType = flightType;
        this.passengerType = passagerType;
        this.spaceType = spaceType;
        cost = 0;
        maxWeight = 50;
        maxLength = 158;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public void setPackage(Package[] p) {
        packages = p;

    }

    public int showCost() {
        return cost;
    }

    public int getFlightType() {
        return flightType;
    }

    public void setFlightType(int flightType) {
        this.flightType = flightType;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    //根据数据库获得该区域航班的行李托运机制
    public Luggage getLuggageBySQL(String spaceType, String flightType) {
        System.out.println(passengerType +"  " + flightType);
        luggages = DataSupport.where("type = ? and areaType = ?",spaceType,flightType).find(Luggage.class);
        if(luggages.size() != 0) {
            return luggages.get(0);
        } else {
            System.out.println("数据库没东西");
            return null;
        }
    }

    public boolean checkAllCosts() {

        //UT中无法使用数据库，因为耦合的关系，直接调用数据库会很不好，因此数据库调用了独立的方法中
        //luggages = DataSupport.where("type = ?",passengerType).where("id = ?",Integer.toString(flightType)).find(Luggage.class);


        Luggage temp = getLuggageBySQL(spaceType,Integer.toString(flightType));
//        Luggage temp = new Luggage(6,2,"公务舱",3,32,158);  //------------------测试接口

        cost = 0;
        boolean isIllegal = false;
        if(temp != null) {

            //非国内航班,因为没有具体的数据没法进行计算
            if(flightType != 1) {

                //美国相关航班
                if(flightType == 8) {
                    maxWeight = 45;

                }
                else {
                    maxWeight = 32;
                }

                for(int i = 0; i < packages.length; i++) {
                    int tempCost;
                    if(i < temp.getSum()) {
                        Log.d("Passager","没超件");
                        System.out.println("没超件");
                        tempCost = getCost(packages[i],temp,false,false);
                    } else if(i == temp.getSum()) {
                        Log.d("Passager","第一次超件");
                        System.out.println("第一次超件");
                        tempCost = getCost(packages[i],temp,true,true);
                    } else {
                        Log.d("Passager","第二次以上超件");
                        System.out.println("第二次以上超件");
                        tempCost = getCost(packages[i],temp,true,false);
                    }
                    if(tempCost == -1) {
                        cost = -1;
                        isIllegal = true;
                        break;
                    } else {
                        System.out.println("cost:" + cost +"  tempCost:" + tempCost);
                        cost += tempCost;
                        System.out.println(cost);
                    }
                }
            }
        }
        return isIllegal;
    }


    //查询当前行李所需额外费用
    public int getCost(Package p,Luggage l,boolean hasOver,boolean firstOver) {
        int tempCost = 0;
        String areaType;
        int overType = 0;
        if(l.getAreaType() == 2 || l.getAreaType() == 3 || l.getAreaType() == 8) {
            areaType = "1";
        } else if(l.getAreaType() == 4) {
            areaType = "2";
        } else if(l.getAreaType() == 9) {
            areaType = "3";
        } else {
            areaType = "4";
        }

        float currentWeight = p.getWeight();
        float currentLength = p.getLength() + p.getWidth() + p.getHeight();


        //无论怎样，只要是大于标准尺寸，就直接pass
        if(currentLength <= l.getMaxLength()) {
            if(currentLength >159 && currentLength < 300) {
                overType = 3;
            }

        } else {
            tempCost = -1;
            return tempCost;
        }

        if(currentWeight <= maxWeight) {
            if(currentWeight > l.getWeight() && currentWeight <= 32) {
                overType = 4;
            } else if(currentWeight > l.getWeight() && currentWeight <= 45) {
                overType = 5;
            } else if(hasOver == true &&firstOver == true) {
                overType = 1;
            } else if(hasOver == true && firstOver == false) {
                overType = 2;
            }

        } else {
            tempCost = -1;
            return tempCost;
        }


        if(overType != 0) {

//            tempCost = test_getCost(overType);   //////////////////测试接口
//            System.out.println(areaType +" " + overType);

            List<CostType> costList = DataSupport.where("areaType = ? and overType = ?",areaType,Integer.toString(overType)).find(CostType.class);
        if(costList.size() !=0) {
                tempCost = costList.get(0).getCost();
            }
        }
        return tempCost;

    }

    private int test_getCost(int costType) {
        if(costType == 1) {
            return 1000;
        } else if(costType == 2) {
            return 2000;
        } else if(costType == 3) {
            return 1000;
        } else if(costType == 4) {
            return 1000;
        } else if(costType == 5) {
            return 3000;
        } else {
            return -1;
        }
    }

}
