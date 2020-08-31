package com.social.englishclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Chart extends AppCompatActivity {

    private String login_id;
    private DatabaseReference mDatabaseRef;
    private List<umd_test> mumd_test;
    private chartdata[] mchartdata;
    private ArrayList<chartdata> umd_chart;
    private ProgressDialog progressDialog;
    private LineChart lineChart;
    private List<String> list_x_axis_name;
    private String[] x_name, y_name;
    private LineData chartData;

    ArrayList<Entry> entry_chart_accent = new ArrayList<>();
    ArrayList<Entry> entry_chart_pronunciation = new ArrayList<>();
    ArrayList<Entry> entry_chart_speed = new ArrayList<>();
    ArrayList<Entry> entry_chart_stress = new ArrayList<>();
    ArrayList<Entry> entry_chart_accent_delete = new ArrayList<>();
    ArrayList<Entry> entry_chart_pronunciation_delete = new ArrayList<>();
    ArrayList<Entry> entry_chart_speed_delete = new ArrayList<>();
    ArrayList<Entry> entry_chart_stress_delete = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent intent = getIntent();
        login_id = intent.getStringExtra("login_id");
        progressDialog = new ProgressDialog(Chart.this);
        progressDialog.setMessage("서버에서 평가목록을 불러오는 중입니다...\n잠시만 기다려주세요");
        progressDialog.show();

        TextView chart_title = (TextView)findViewById(R.id.chart_title);


        RadioButton radio_all_btn = (RadioButton)findViewById(R.id.radio_all_btn);
        radio_all_btn.isSelected();
        RadioButton radio_accent_btn = (RadioButton)findViewById(R.id.radio_accent_btn);
        RadioButton radio_pronunciation_btn = (RadioButton)findViewById(R.id.radio_pronunciation_btn);
        RadioButton radio_speed_btn = (RadioButton)findViewById(R.id.radio_speed_btn);
        RadioButton radio_stress_btn = (RadioButton)findViewById(R.id.radio_stress_btn);

        View.OnClickListener Listener_chart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    switch (v.getId()) {
                        case R.id.radio_all_btn:
                            entry_chart_accent.addAll(entry_chart_accent_delete);
                            entry_chart_pronunciation.addAll(entry_chart_pronunciation_delete);
                         entry_chart_speed.addAll(entry_chart_speed_delete);
                         entry_chart_stress.addAll(entry_chart_stress_delete);
                            lineChart.invalidate();
                            break;
                        case R.id.radio_accent_btn:

                            entry_chart_accent.addAll(entry_chart_accent_delete);
                            entry_chart_pronunciation.clear();
                            entry_chart_speed.clear();
                            entry_chart_stress.clear();
                            lineChart.invalidate();
//                        entry_chart_pronunciation.addAll(entry_chart_pronunciation_delete);
//                         entry_chart_speed.addAll(entry_chart_speed_delete);
//                         entry_chart_stress.addAll(entry_chart_stress_delete);


                            break;
                        case R.id.radio_pronunciation_btn:
                            entry_chart_pronunciation.addAll(entry_chart_pronunciation_delete);
                            entry_chart_accent.clear();
                            entry_chart_speed.clear();
                            entry_chart_stress.clear();
                            lineChart.invalidate();
//                            entry_chart_accent.addAll(entry_chart_accent_delete);
//                            entry_chart_speed.addAll(entry_chart_speed_delete);
//                            entry_chart_stress.addAll(entry_chart_stress_delete);

                            break;
                        case R.id.radio_speed_btn:
                            entry_chart_speed.addAll(entry_chart_speed_delete);
                            entry_chart_accent.clear();
                            entry_chart_pronunciation.clear();
                            entry_chart_stress.clear();
                            lineChart.invalidate();
                            break;
                        case R.id.radio_stress_btn:
                            entry_chart_stress.addAll(entry_chart_stress_delete);
                            entry_chart_accent.clear();
                            entry_chart_pronunciation.clear();
                            entry_chart_speed.clear();
                            lineChart.invalidate();
                            break;
                    }
                }


            };
        radio_all_btn.setOnClickListener(Listener_chart);
        radio_accent_btn.setOnClickListener(Listener_chart);
        radio_pronunciation_btn.setOnClickListener(Listener_chart);
        radio_speed_btn.setOnClickListener(Listener_chart);
        radio_stress_btn.setOnClickListener(Listener_chart);




        mumd_test = new ArrayList<>();
        umd_chart = new ArrayList<chartdata>();
        list_x_axis_name = new ArrayList<String>();

        lineChart = (LineChart) findViewById(R.id.chart);   //layout의 id
        chartData = new LineData();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("umd_test");

        chartdata_load();



    }
public void chartdata_load(){
    mDatabaseRef.orderByKey().addValueEventListener(new ValueEventListener() {


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Boolean found;
            mumd_test.clear();
            umd_chart.clear();
            mchartdata = new chartdata[20];
            int i =0;
            float accent_y=0;
            float pronunciation_y=0;
            float speed_y=0;
            float stress_y=0;

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                String searchname = postSnapshot.getKey();
                found = searchname.contains(login_id);

                if(found){
//                        String key = postSnapshot.get.toString();
//umd_test의 해당파일명 아래의 자동생성키 목록 아래의 4종류 평가 값을 담는다.
                    umd_test key =postSnapshot.getChildren().iterator().next().getValue(umd_test.class);
                    chartdata u_key = postSnapshot.getChildren().iterator().next().getValue(chartdata.class);
//                        String key = mDatabaseRef.child(searchname).push().getKey();
                    Log.e("자동생성 키", ""+key.getaccent()+key.getpronunciation()+key.getspeed()+key.getstress());
//                        umd_test umdTest = postSnapshot.getValue(umd_test.class);
                    mumd_test.add(key);
//                    umd_chart.add(u_key);
                    chartdata data = new chartdata(key.getstress(),key.getaccent(),key.getspeed(),key.getpronunciation(),searchname.substring(searchname.lastIndexOf("_")+1));
                    umd_chart.add(data);
//                    mchartdata[i] = new chartdata(key.getstress(),key.getaccent(),key.getspeed(),key.getpronunciation(),searchname.substring(searchname.lastIndexOf("_")+1));
//                    mchartdata.add(key.getstress(),key.getaccent(),key.getspeed(),key.getpronunciation(),searchname);
                    umd_test umd_data = mumd_test.get(i);
                    Log.e("mumd_test 에 추가됨 ", "searchname"+searchname+"  값 "+umd_data.getaccent());
//                    list_x_axis_name.add(searchname.substring(searchname.lastIndexOf("_")+1));
//                    System.arraycopy(list_x_axis_name,0,umd_chart,umd_chart.size(),list_x_axis_name.size());

/*
                    if(umd_data.getaccent() !=null) {
                        switch (umd_data.getaccent()) {
                            case "상":
                                accent_y = 3;
                                break;
                            case "좋아요.":
                                accent_y = 3;
                                break;
                            case "중":
                                accent_y = 2;
                                break;
                            case "괜찮아요.":
                                accent_y = 2;
                                break;
                            case "하":
                                accent_y = 1;
                                break;
                            case "힘내세요.":
                                accent_y = 1;
                                break;
                            case "":
                                accent_y = 1;
                                break;
                        }
                    }
                    entry_chart_accent.add(new Entry(i, accent_y));
//                    String file = searchname.substring(searchname.lastIndexOf("_")+1);
//                    entry_chart_accent.add(new Entry(Integer.parseInt(file.substring(2,4)+file.substring(6,8)+file.substring(10,12)), accent_y));
                    Log.e("엔트리 차트 악센트 배열",""+entry_chart_accent);
                    if(umd_data.getpronunciation() !=null) {
                        switch (umd_data.getpronunciation()) {
                            case "상":
                                pronunciation_y = 3;
                                break;
                            case "좋아요.":
                                pronunciation_y = 3;
                                break;
                            case "중":
                                pronunciation_y = 2;
                                break;
                            case "괜찮아요.":
                                pronunciation_y = 2;
                                break;
                            case "하":
                                pronunciation_y = 1;
                                break;
                            case "힘내세요.":
                                pronunciation_y = 1;
                                break;
                            case "":
                                pronunciation_y = 1;
                                break;
                        }
                    }
                    entry_chart_pronunciation.add(new Entry(i, pronunciation_y));

                    if(umd_data.getspeed() !=null) {
                        switch (umd_data.getspeed()) {
                            case "상":
                                speed_y = 3;
                                break;
                            case "좋아요.":
                                speed_y = 3;
                                break;
                            case "중":
                                speed_y = 2;
                                break;
                            case "괜찮아요.":
                                speed_y = 2;
                                break;
                            case "하":
                                speed_y = 1;
                                break;
                            case "힘내세요.":
                                speed_y = 1;
                                break;
                            case "":
                                speed_y = 1;
                                break;
                        }
                    }
                    entry_chart_speed.add(new Entry(i, speed_y));
                    if(umd_data.getstress() !=null) {
                        switch (umd_data.getstress()) {
                            case "상":
                                stress_y = 3;
                                break;
                            case "좋아요.":
                                stress_y = 3;
                                break;
                            case "중":
                                stress_y = 2;
                                break;
                            case "괜찮아요.":
                                stress_y = 2;
                                break;
                            case "하":
                                stress_y = 1;
                                break;
                            case "힘내세요.":
                                stress_y = 1;
                                break;
                            case "":
                                stress_y = 1;
                                break;
                        }
                    }
                    entry_chart_stress.add(new Entry(i, stress_y));
//                        entry_chart.add(new Entry(searchname.substring(searchname.lastIndexOf("_")+1), umd_data.getaccent()));

 */
                    i++;
                }


//                    i++;
            }
//            Arrays.sort(mchartdata);
            Collections.sort(umd_chart);

            for (int j=0; j<i;j++) {
//                      chartdata umd_chart_data = mchartdata[j];
                Log.e("합쳐진 umd_chart 배열",""+ umd_chart.get(j).getfilename()+umd_chart.get(j).getaccent());

// x값 y값 좌표 배열에 입력
                if(umd_chart.get(j).getaccent() !=null) {
                    switch (umd_chart.get(j).getaccent()) {
                        case "상":
                            accent_y = 3;
                            break;
                        case "좋아요.":
                            accent_y = 3;
                            break;
                        case "중":
                            accent_y = 2;
                            break;
                        case "괜찮아요.":
                            accent_y = 2;
                            break;
                        case "하":
                            accent_y = 1;
                            break;
                        case "힘내세요.":
                            accent_y = 1;
                            break;
                        case "":
                            accent_y = 1;
                            break;
                    }
                }
                entry_chart_accent.add(new Entry(j, accent_y ));
//                    String file = searchname.substring(searchname.lastIndexOf("_")+1);
//                    entry_chart_accent.add(new Entry(Integer.parseInt(file.substring(2,4)+file.substring(6,8)+file.substring(10,12)), accent_y));
                Log.e("엔트리 차트 악센트 배열",""+entry_chart_accent);
                if(umd_chart.get(j).getpronunciation() !=null) {
                    switch (umd_chart.get(j).getpronunciation()) {
                        case "상":
                            pronunciation_y = 3;
                            break;
                        case "좋아요.":
                            pronunciation_y = 3;
                            break;
                        case "중":
                            pronunciation_y = 2;
                            break;
                        case "괜찮아요.":
                            pronunciation_y = 2;
                            break;
                        case "하":
                            pronunciation_y = 1;
                            break;
                        case "힘내세요.":
                            pronunciation_y = 1;
                            break;
                        case "":
                            pronunciation_y = 1;
                            break;
                    }
                }
                entry_chart_pronunciation.add(new Entry(j, pronunciation_y));

                if(umd_chart.get(j).getspeed() !=null) {
                    switch (umd_chart.get(j).getspeed()) {
                        case "상":
                            speed_y = 3;
                            break;
                        case "좋아요.":
                            speed_y = 3;
                            break;
                        case "중":
                            speed_y = 2;
                            break;
                        case "괜찮아요.":
                            speed_y = 2;
                            break;
                        case "하":
                            speed_y = 1;
                            break;
                        case "힘내세요.":
                            speed_y = 1;
                            break;
                        case "":
                            speed_y = 1;
                            break;
                    }
                }
                entry_chart_speed.add(new Entry(j, speed_y));
                if(umd_chart.get(j).getstress() !=null) {
                    switch (umd_chart.get(j).getstress()) {
                        case "상":
                            stress_y = 3;
                            break;
                        case "좋아요.":
                            stress_y = 3;
                            break;
                        case "중":
                            stress_y = 2;
                            break;
                        case "괜찮아요.":
                            stress_y = 2;
                            break;
                        case "하":
                            stress_y = 1;
                            break;
                        case "힘내세요.":
                            stress_y = 1;
                            break;
                        case "":
                            stress_y = 1;
                            break;
                    }
                }
                entry_chart_stress.add(new Entry(j, stress_y));
                list_x_axis_name.add(umd_chart.get(j).getfilename());
            }



            entry_chart_accent_delete.addAll(entry_chart_accent);
            entry_chart_pronunciation_delete.addAll(entry_chart_pronunciation);
            entry_chart_speed_delete.addAll(entry_chart_speed);
            entry_chart_stress_delete.addAll(entry_chart_stress);
//x축 설정
//x축 이름 정렬
//            Collections.sort(list_x_axis_name);
            x_name = new String[list_x_axis_name.size()];
            x_name = list_x_axis_name.toArray(x_name);

            Log.e("x축 네임", ""+ x_name.toString());

            XAxis xAxis=lineChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(x_name));

//                xAxis.setCenterAxisLabels(true);
            xAxis.setLabelRotationAngle(-90);
//y축 설정
            List<String> yarr = new ArrayList<String>();
            yarr.add("");
            yarr.add("하");
            yarr.add("중");
            yarr.add("상");

            y_name = new String[yarr.size()];
            y_name = yarr.toArray(y_name);

            YAxis yAxis=lineChart.getAxisLeft();
            yAxis.setGranularity(1);
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(4);
            yAxis.setTextSize(18);
            yAxis.setValueFormatter(new IndexAxisValueFormatter(y_name));

            lineChart.getAxisRight().setDrawLabels(false);
            lineChart.getAxisRight().setDrawGridLines(false);




accent_chart();
pronunciation_chart();
speed_chart();
stress_chart();


            lineChart.setData(chartData);
            lineChart.invalidate();

//                for (int i=0; i<mumd_test.size();i++) {
//                Log.e("umd_data 에 추가됨 ", " "+mumd_test.get(0));
//                    umd_test umd_data = mumd_test.get(0);
//                    Log.e("umd_data 에 추가됨 ", " "+umd_data.);
//                }


            //시간역순 정렬
//                Collections.sort(mUploads, sortByTotalCall);

            try {
                progressDialog.dismiss();
            } catch (Exception e) {
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(Chart.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

        }


    });
}

public void accent_chart(){
    //라인차트 설정(accent)
    LineDataSet lineDataSet_accent = new LineDataSet(entry_chart_accent, "억양");
    lineDataSet_accent.setLineWidth(2); // 선 굵기
    lineDataSet_accent.setCircleRadius(6); // 곡률
    lineDataSet_accent.setCircleColor(Color.parseColor("#CD1039")); // LineChart에서 Line Circle Color 설정
    lineDataSet_accent.setCircleHoleColor(Color.parseColor("#CD1039")); // LineChart에서 Line Hole Circle Color 설정
    lineDataSet_accent.setColor(Color.parseColor("#CD1039")); // LineChart에서 Line Color 설정

    chartData.addDataSet(lineDataSet_accent);
}
public void pronunciation_chart(){
    //라인차트 설정(pronunciation)
    LineDataSet lineDataSet_pronunciation = new LineDataSet(entry_chart_pronunciation, "발음");
    lineDataSet_pronunciation.setLineWidth(2); // 선 굵기
    lineDataSet_pronunciation.setCircleRadius(6); // 곡률
    lineDataSet_pronunciation.setCircleColor(Color.parseColor("#5EC75E")); // LineChart에서 Line Circle Color 설정
    lineDataSet_pronunciation.setCircleHoleColor(Color.parseColor("#5EC75E")); // LineChart에서 Line Hole Circle Color 설정
    lineDataSet_pronunciation.setColor(Color.parseColor("#5EC75E")); // LineChart에서 Line Color 설정
    chartData.addDataSet(lineDataSet_pronunciation);
}
public void speed_chart(){
    //라인차트 설정(speed)
    LineDataSet lineDataSet_speed = new LineDataSet(entry_chart_speed, "머뭇거림");
    lineDataSet_speed.setLineWidth(2); // 선 굵기
    lineDataSet_speed.setCircleRadius(6); // 곡률
    lineDataSet_speed.setCircleColor(Color.parseColor("#2828CD")); // LineChart에서 Line Circle Color 설정
    lineDataSet_speed.setCircleHoleColor(Color.parseColor("#2828CD")); // LineChart에서 Line Hole Circle Color 설정
    lineDataSet_speed.setColor(Color.parseColor("#2828CD")); // LineChart에서 Line Color 설정
    chartData.addDataSet(lineDataSet_speed);
}

    public void stress_chart(){
//라인차트 설정(stress)
        LineDataSet lineDataSet_stress = new LineDataSet(entry_chart_stress, "강세");
        lineDataSet_stress.setLineWidth(2); // 선 굵기
        lineDataSet_stress.setCircleRadius(6); // 곡률
        lineDataSet_stress.setCircleColor(Color.parseColor("#FFB400")); // LineChart에서 Line Circle Color 설정
        lineDataSet_stress.setCircleHoleColor(Color.parseColor("#FFB400")); // LineChart에서 Line Hole Circle Color 설정
        lineDataSet_stress.setColor(Color.parseColor("#FFB400")); // LineChart에서 Line Color 설정
        chartData.addDataSet(lineDataSet_stress);
    }
    private final static Comparator<chartdata> sortByfilename= new Comparator<chartdata>() {


        @Override
        public int compare(chartdata object1, chartdata object2) {
            return Collator.getInstance().compare(object1.getfilename(), object2.getfilename());
        }

    };

}
