package com.mightted.nanhangluggage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mightted.nanhangluggage.luggage.CostType;
import com.mightted.nanhangluggage.luggage.Luggage;
import com.mightted.nanhangluggage.luggage.Package;
import com.mightted.nanhangluggage.luggage.Passager;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    public static int FLIGHT_DOMESTIC = 0;  //国内航班，不包括兰州和乌鲁木齐
    public static int FLIGHT_DOMESTIC_SPECIAL = 1;  //国内航班，限定为兰州和乌鲁木齐
    public static int FLIGHT_JAPANETC = 2;  //日本，美洲方面的航班
    public static int FLIGHT_SINGAPORE = 3;  //新加坡的航班
    public static int FLIGHT_CENTRAL_WEST_ASIA = 4;  //中西亚地区
    public static int FLIGHT_NAIROBI = 5;  //内罗毕地区
    public static int FLIGHT_KOREA = 6;  //韩国地区
    public static int FLIGHT_DUBAI = 7;  //迪拜地区
    public static int FLIGHT_USA = 8;  //美国地区
    public static int FLIGHT_OTHER = 9;  //其他地区

    public static int AREA_DOMESTIC = 1;  //国内行李情况
    public static int AREA_JAPAN = 2; //前往日本方面的行李情况，包括内罗毕

    public static int AREA_LAN_DUBAI = 3;  //兰州、乌鲁木齐与迪拜之间的行李情况
    public static int AREA_CENTRAL_WEST_ASIA = 4;  //中西亚方面的行李情况
//    public static int AREA_NAIROBI = 3;  //内罗毕方面的行李情况
    public static int AREA_OTHER = 5;  //其他方面的行李情况
    public static int AREA_KOREA = 6;  //韩国方面的行李情况

    public static int AREA_NONE = 7;  //无相关航班
    public static int AREA_USA = 8; //美国相关航班
    public static int AREA_NAIROBI = 9;  //内罗毕方面的行李情况

    Spinner chooseTypeSpr; //选择航班类型
    Spinner passengerSpr;  //选择乘客类型
    Spinner srcSpinner;
    Spinner dstSpinner;
    Spinner spaceSpinner;  //选择舱位类型
    LinearLayout linearLayout1;
    LinearLayout showLugLayout;
    LinearLayout spaceTypeLayout;
    LinearLayout spaceTypeLayout1;
    LinearLayout spaceTypeLayout2;
    TextView showCostText;
    int currentSrc,currentDst;
    boolean isNoSeatChild;  //判断是不是不占座婴儿
    boolean isHadSpace;  //判断是否已经选择了舱位，这里主要是用于当两个舱位下拉框都消失的时候置为false;

    private  List<View> luggageList = new ArrayList<>();

    int luggageStatus; //记录区域类型的状态
    String spaceType; //记录舱位的类型
    String passengerType;  //记录乘客的类型
    Package[] mPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentSrc = 0;
        currentDst = 0;
        isNoSeatChild = false;
        isHadSpace = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                LitePal.getDatabase();
                init();
            }
        }).start();

        chooseTypeSpr = (Spinner) findViewById(R.id.choose_type_spinner);
        passengerSpr = (Spinner) findViewById(R.id.passenger_spinner);
        srcSpinner = (Spinner) findViewById(R.id.src_spinner);
        dstSpinner = (Spinner) findViewById(R.id.dst_spinner);
        linearLayout1 = (LinearLayout) findViewById(R.id.layout_area);
        showLugLayout = (LinearLayout) findViewById(R.id.show_luggage_layout);
        spaceTypeLayout = (LinearLayout) findViewById(R.id.space_type_layout);
        spaceTypeLayout1 = (LinearLayout)spaceTypeLayout.findViewWithTag("space_type_layout1");
        spaceTypeLayout2 = (LinearLayout)spaceTypeLayout.findViewWithTag("space_type_layout2");
        showCostText = (TextView) findViewById(R.id.show_cost_text);
        spaceSpinner = (Spinner) spaceTypeLayout1.findViewWithTag("space_type");



        //选择航班类型
        chooseTypeSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //航班为国际航班时显示完整航线，否则隐藏
                if(i==1) {
                    linearLayout1.setVisibility(View.VISIBLE);
                    showCostText.setText("");
                } else {
                    linearLayout1.setVisibility(View.GONE);
                    showCostText.setText("当前不支持计算国内航班");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //选择乘客类型
        passengerSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //选择不占座婴儿时，将隐藏选择舱位的下拉框
                if(i == 1) {
                    isNoSeatChild = true;
                } else {
                    isNoSeatChild = false;
                }
                passengerType = adapterView.getSelectedItem().toString();
                showLuggageType();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        srcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSrc = i;
                showLuggageType();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentDst = i;
                showLuggageType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setFlightSpace();


    }

    private void setFlightSpace() {
        spaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("MainActivity",adapterView.getItemAtPosition(i).toString());
                spaceType = adapterView.getSelectedItem().toString(); //这个和上面那一句效果一样
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //添加行李选项
    public void addLuggage(View view) {
        View layoutView = LayoutInflater.from(this).inflate(R.layout.layout_luggage,null);
        luggageList.add(layoutView);

        showLugLayout.addView(layoutView);


    }

    //删除最后一个行李
    public void removeLug(View view) {
        if(luggageList.size() != 0) {
            showLugLayout.removeView(luggageList.get(luggageList.size()-1));
            luggageList.remove(luggageList.size()-1);
        }
    }

    //计算当前行李所需费用
    public void checkCost(View view) {
        if(passengerType.equals("成人、儿童、占座婴儿")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Passager passager = new Passager(luggageStatus,passengerType,spaceType);
                    if(getPackagesFromView()) {
                        passager.setPackage(mPackages);
                        if(!passager.checkAllCosts()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showCostText.setText(Integer.toString(passager.showCost()));
                                }
                            });
                        } else {
                            //这里的情况就是行李中出现了违规的物品，也就是超过规定的重量和尺寸之类的
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showCostText.setText("行李超重或超尺寸，不允许带上飞机");
                                }
                            });
                        }
                    }

                }
            }).start();
        }
//        List<Luggage> luggages = DataSupport.where("type = ? and areaType = ?","公务舱","2").find(Luggage.class); //测试接口
//        Luggage l =luggages.get(0);
//        Log.d("MainActivity",Integer.toString(l.getId()));
//        Log.d("MainActivity",Integer.toString(l.getAreaType()));
//        Log.d("MainActivity",l.getType());
//        Log.d("MainActivity",Float.toString(l.getWeight()));
//        Log.d("MainActivity",Integer.toString(l.getSum()));
//        Log.d("MainActivity",Float.toString(l.getMaxLength()));

    }

    //根据航班始发地和目的地所确定行李额机制
    private int chooseLuggageArea(int src, int dst) {
        //航班在迪拜和兰州、乌鲁木齐之间
        if((src == FLIGHT_DOMESTIC_SPECIAL && dst == FLIGHT_DUBAI) || (dst == FLIGHT_DOMESTIC_SPECIAL && src == FLIGHT_DUBAI)) {
            return AREA_LAN_DUBAI;
        }
        if((src == FLIGHT_DOMESTIC && dst == FLIGHT_DUBAI) || (dst == FLIGHT_DOMESTIC && src == FLIGHT_DUBAI)) {
            return AREA_JAPAN;
        }

        //当始发地在中国时。这里为了防止有人点了特殊地区（兰州、乌鲁木齐地区），因此两个都做了判断
        if(src ==FLIGHT_DOMESTIC || src == FLIGHT_DOMESTIC_SPECIAL) {
            if(dst == FLIGHT_JAPANETC) {
                //前往日本等地区
                return AREA_JAPAN;
            } else if(dst == FLIGHT_SINGAPORE || dst == FLIGHT_OTHER) {
                //前往其他地区，以及去新加坡都是这个行李机制
                return AREA_OTHER;
            } else if(dst == FLIGHT_CENTRAL_WEST_ASIA) {
                //前往中西亚
                return AREA_CENTRAL_WEST_ASIA;
            } else if(dst == FLIGHT_NAIROBI) {
                //前往内罗毕
                return AREA_NAIROBI;
            } else if(dst == FLIGHT_KOREA) {
                //前往韩国
                return AREA_KOREA;
            } else if(dst == FLIGHT_DOMESTIC || dst == FLIGHT_DOMESTIC_SPECIAL) {
                //为防止有人在国际航班中选择国内航班，因此在这里做了兼容
                return AREA_DOMESTIC;
            }
                else if(dst == FLIGHT_USA) {
                    return AREA_USA;
            } else {
                return AREA_NONE;
            }

            //始发地为外国，目的地为中国的航班
        } else if(dst ==FLIGHT_DOMESTIC || dst == FLIGHT_DOMESTIC_SPECIAL) {

            if(src == FLIGHT_JAPANETC || src == FLIGHT_SINGAPORE) {
                return AREA_JAPAN;
            } else if( src == FLIGHT_OTHER) {
                return AREA_OTHER;
            } else if(src == FLIGHT_CENTRAL_WEST_ASIA) {
                return AREA_CENTRAL_WEST_ASIA;
            } else if(src == FLIGHT_NAIROBI) {
                return AREA_NAIROBI;
            } else if(src == FLIGHT_KOREA) {
                return AREA_KOREA;
            } else if(src == FLIGHT_USA) {
                return AREA_USA;
            }
            else {
                return AREA_NONE;
            }

        } else {
            return AREA_NONE;
        }
    }


    /**
     * 根据反馈情况，显示或者隐藏舱位选择
     */
    private void showLuggageType() {
        int flightType;
        if(isNoSeatChild == true) {
            flightType=AREA_NONE;
        } else {
            flightType = chooseLuggageArea(currentSrc,currentDst);
            luggageStatus = flightType;
        }
        if(flightType == AREA_DOMESTIC || flightType == AREA_KOREA) {
            spaceTypeLayout1.setVisibility(View.VISIBLE);
            spaceTypeLayout2.setVisibility(View.GONE);
            spaceSpinner = (Spinner) spaceTypeLayout1.findViewWithTag("space_type");
            isHadSpace = true;
            setFlightSpace();
//            Log.d("MainActivity","国内航班");

        } else if(flightType == AREA_NONE) {
            spaceTypeLayout1.setVisibility(View.GONE);
            spaceTypeLayout2.setVisibility(View.GONE);
            isHadSpace = false;
        } else {
            spaceTypeLayout1.setVisibility(View.GONE);
            spaceTypeLayout2.setVisibility(View.VISIBLE);
            spaceSpinner = (Spinner) spaceTypeLayout2.findViewWithTag("space_type");
            isHadSpace = true;
            setFlightSpace();
//            Log.d("MainActivity","国外航班");
        }
    }

    /**
     * 从行李参数输入框中抽取行李参数
     */
    private boolean getPackagesFromView() {
        boolean checkData = false;
        int pCount = luggageList.size();
        mPackages = new Package[pCount];

        for(int i = 0; i < pCount; i++) {
            checkData = true;
            Package tPackage = new Package();

            TextView tempWeight = (TextView)luggageList.get(i).findViewWithTag("luggage_weight_text");
            String weightText = tempWeight.getText().toString();
            if(TextUtils.isEmpty(weightText) || !isInteger(weightText)) {
                checkData = false;
                break;
            } else {
                tPackage.setWeight(Float.parseFloat(weightText));
            }

            TextView tempLength = (TextView)luggageList.get(i).findViewWithTag("luggage_length_text");
            String lengthText = tempLength.getText().toString();
            if(TextUtils.isEmpty(lengthText) || !isInteger(lengthText)) {
                checkData = false;
                break;
            } else {
                tPackage.setLength(Float.parseFloat(lengthText));
            }

            TextView tempWidth = (TextView)luggageList.get(i).findViewWithTag("luggage_width_text");
            String widthText = tempWidth.getText().toString();
            if(TextUtils.isEmpty(widthText) || !isInteger(widthText)) {
                checkData = false;
                break;
            } else {
                tPackage.setWidth(Float.parseFloat(widthText));
            }

            TextView tempHeight = (TextView)luggageList.get(i).findViewWithTag("luggage_height_text");
            String heightText = tempHeight.getText().toString();
            if(TextUtils.isEmpty(heightText) || !isInteger(heightText)) {
                checkData = false;
                break;
            } else {
                tPackage.setHeight(Float.parseFloat(heightText));
            }
            mPackages[i] = tPackage;
        }
        return checkData;
    }

    private void init() {
        //国内
        Luggage temp = new Luggage(1,1,"头等舱",1,40,200); //这里我不确定执行操作后是否应该对temp设为null
        temp.save();
        temp = new Luggage(1,1,"公务舱",1,30,200);
        temp.save();
        temp = new Luggage(1,1,"经济舱",1,20,200);
        temp.save();
        temp = new Luggage(1,1,"婴儿",1,10,200);
        temp.save();

        //日本等地区和内罗毕
        temp = new Luggage(1,2,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(1,2,"公务舱",2,32,158);
        temp.save();
        temp = new Luggage(1,2,"明珠经济舱",2,23,158);
        temp.save();
        temp = new Luggage(1,2,"经济舱",2,23,158);
        temp.save();
        temp = new Luggage(1,2,"不占座婴儿",1,10,115);
        temp.save();

        //迪拜地区
        temp = new Luggage(1,3,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(1,3,"公务舱",2,32,158);
        temp.save();
        temp = new Luggage(1,3,"明珠经济舱",1,32,158);
        temp.save();
        temp = new Luggage(1,2,"经济舱",1,23,158);
        temp.save();
        temp = new Luggage(1,2,"不占座婴儿",1,10,115);
        temp.save();

        //中西亚地区
        temp = new Luggage(1,4,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(1,4,"公务舱",2,32,158);
        temp.save();
        temp = new Luggage(1,4,"明珠经济舱",1,32,158);
        temp.save();
        temp = new Luggage(1,4,"经济舱",1,32,158);
        temp.save();
        temp = new Luggage(1,4,"不占座婴儿",1,10,115);
        temp.save();

        //其他地区（包括中国至新加坡）
        temp = new Luggage(1,5,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(1,5,"公务舱",3,23,158);
        temp.save();
        temp = new Luggage(1,5,"明珠经济舱",2,23,158);
        temp.save();
        temp = new Luggage(1,5,"经济舱",1,23,158);
        temp.save();
        temp = new Luggage(1,5,"不占座婴儿",1,10,115);
        temp.save();

        //韩国地区
        temp = new Luggage(1,6,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(1,6,"公务舱",2,23,158);
        temp.save();
        temp = new Luggage(1,6,"经济舱",1,23,158);
        temp.save();
        temp = new Luggage(1,6,"不占座婴儿",1,10,115);
        temp.save();

        //美国方面航班
        temp = new Luggage(8,2,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(8,2,"公务舱",2,32,158);
        temp.save();
        temp = new Luggage(8,2,"明珠经济舱",2,23,158);
        temp.save();
        temp = new Luggage(8,2,"经济舱",2,23,158);
        temp.save();
        temp = new Luggage(8,2,"不占座婴儿",1,10,115);
        temp.save();

        //内罗毕方面航班
        temp = new Luggage(9,2,"头等舱",3,32,158);
        temp.save();
        temp = new Luggage(9,2,"公务舱",2,32,158);
        temp.save();
        temp = new Luggage(9,2,"明珠经济舱",2,23,158);
        temp.save();
        temp = new Luggage(9,2,"经济舱",2,23,158);
        temp.save();
        temp = new Luggage(9,2,"不占座婴儿",1,10,115);
        temp.save();

        CostType costType = new CostType(1,1,1000);
        costType.save();
        costType = new CostType(1,2,2000);
        costType.save();
        costType = new CostType(1,3,1000);
        costType.save();
        costType = new CostType(1,4,1000);
        costType.save();
        costType = new CostType(1,5,3000);
        costType.save();

        costType = new CostType(2,1,450);
        costType.save();
        costType = new CostType(2,2,1300);
        costType.save();
        costType = new CostType(2,3,1000);
        costType.save();
        costType = new CostType(2,4,3000);
        costType.save();
        costType = new CostType(2,5,3000);
        costType.save();

        costType = new CostType(3,1,1000);
        costType.save();
        costType = new CostType(3,2,2000);
        costType.save();
        costType = new CostType(3,3,1000);
        costType.save();
        costType = new CostType(3,4,2000);
        costType.save();
        costType = new CostType(3,5,3000);
        costType.save();

        costType = new CostType(4,1,450);
        costType.save();
        costType = new CostType(4,2,1300);
        costType.save();
        costType = new CostType(4,3,1000);
        costType.save();
        costType = new CostType(4,4,1000);
        costType.save();
        costType = new CostType(4,5,3000);
        costType.save();
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
