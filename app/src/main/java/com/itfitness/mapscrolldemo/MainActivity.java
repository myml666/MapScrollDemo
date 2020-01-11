package com.itfitness.mapscrolldemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yinglan.scrolllayout.ScrollLayout;
import com.yinglan.scrolllayout.content.ContentRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MapView activityMainMv;
    private AMap aMap;
    private ContentRecyclerView activityMainRv;
    private ScrollLayout activityMainSl;
    private BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter;
    private FrameLayout activityMainTitleContainer;
    private ImageView activityMainImgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        activityMainMv.onCreate(savedInstanceState);
        initMapDatas();
        initScrollLayout();
        initListDatas();
    }

    private void initListDatas() {
        ArrayList<String> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add(i + "");
        }
        initAdapter(datas);
    }

    private void initAdapter(ArrayList<String> datas) {
        if (baseQuickAdapter == null) {
            baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_list, datas) {
                @Override
                protected void convert(BaseViewHolder helper, String item) {
                    helper.setText(R.id.item_list_tv, item);
                }
            };
            activityMainRv.setLayoutManager(new LinearLayoutManager(this));
            View inflate = View.inflate(this, R.layout.view_header, null);
            baseQuickAdapter.addHeaderView(inflate);
            activityMainRv.setAdapter(baseQuickAdapter);
        } else {
            baseQuickAdapter.setNewData(datas);
            baseQuickAdapter.notifyDataSetChanged();
        }
    }

    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                activityMainSl.getBackground().setAlpha(255 - (int) precent);
                activityMainTitleContainer.getBackground().setAlpha(255 - (int) precent);
            }
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            switch (currentStatus) {
                case OPENED:
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    break;
                case EXIT:
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    break;
                case CLOSED:
                    break;
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };

    /**
     * 加载滑动布局
     */
    private void initScrollLayout() {
        activityMainSl.setMinOffset(0);
        activityMainSl.setMaxOffset((int) (ScreenUtils.getScreenHeight() * 0.7));
        activityMainSl.setExitOffset((int) (ScreenUtils.getScreenHeight() * 0.3));
        activityMainSl.setIsSupportExit(true);
        activityMainSl.setAllowHorizontalScroll(true);
//        activityMainSl.setToExit();
        activityMainSl.setOnScrollChangedListener(mOnScrollChangedListener);
    }

    /**
     * 加载地图相关数据
     */
    private void initMapDatas() {
        initAMap();
        initCurrentLocation();
        initZoom();
    }

    /**
     * 加载缩放级别
     */
    private void initZoom() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    /**
     * 加载当前位置蓝点
     */
    private void initCurrentLocation() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void initAMap() {
        if (aMap == null) {
            aMap = activityMainMv.getMap();
        }
    }

    private void initView() {
        activityMainMv = (MapView) findViewById(R.id.activity_main_mv);
        activityMainRv = (ContentRecyclerView) findViewById(R.id.activity_main_rv);
        activityMainSl = (ScrollLayout) findViewById(R.id.activity_main_sl);
        activityMainTitleContainer = (FrameLayout) findViewById(R.id.activity_main_title_container);
        activityMainImgBack = (ImageView) findViewById(R.id.activity_main_img_back);
        activityMainSl.getBackground().setAlpha(255);
        activityMainTitleContainer.getBackground().setAlpha(255);
        activityMainImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        activityMainMv.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        activityMainMv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        activityMainMv.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        activityMainMv.onSaveInstanceState(outState);
    }
}
