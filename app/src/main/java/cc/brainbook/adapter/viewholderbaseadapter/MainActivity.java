package cc.brainbook.adapter.viewholderbaseadapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {
    private List<Map<String, Object>> mData;
    //    MyAdapter adapter;
    ViewHolderBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mData = getData();
//        adapter = new MyAdapter(this);

        ///BaseAppAdapter
        adapter = new ViewHolderBaseAdapter(mData, R.layout.vlist2) {
            @Override
            protected void bindView(ViewHolder vHolder, Object data) {
                ImageView viewImg = vHolder.getView(R.id.img);
                Object a = ((HashMap)data).get("img");
                viewImg.setImageResource(Integer.parseInt(a.toString()));

                TextView viewTitle = vHolder.getView(R.id.title);
                viewTitle.setText(((HashMap)data).get("title").toString());

                TextView viewInfo = vHolder.getView(R.id.info);
                viewInfo.setText(((HashMap)data).get("info").toString());
            }
        };

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ///adapter.sort()
        ///https://stackoverflow.com/questions/9906464/sort-listview-with-array-adapter
        adapter.sort(new Comparator<HashMap>() {
            @Override
            public int compare(HashMap lhs, HashMap rhs) {
                return lhs.get("title").toString().compareTo(rhs.get("title").toString());
//                return lhs.compareTo(rhs);
            }
        });

        setListAdapter(adapter);
    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "G1");
        map.put("info", "google 1");
        map.put("img", R.drawable.i1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "G2");
        map.put("info", "google 2");
        map.put("img", R.drawable.i2);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "G3");
        map.put("info", "google 3");
        map.put("img", R.drawable.i3);
        list.add(map);

        return list;
    }

    // ListView 中某项被选中后的逻辑
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Log.v("TAG", (String)mData.get(position).get("title"));
//        showInfo();
//        adapter.remove(position);
        adapter.clear();
    }

    /**
     * listview中点击按键弹出对话框
     */
    public void showInfo(){
        new AlertDialog.Builder(this)
                .setTitle("我的listview")
                .setMessage("介绍...")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

    }



    public final class ViewHolder{
        public ImageView img;
        public TextView title;
        public TextView info;
        public Button viewBtn;
    }



    /**
     * 删除按钮的监听接口
     */
    public interface onItemDeleteListener {
        void onDeleteClick(int i);
    }

    private onItemDeleteListener mOnItemDeleteListener;

    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener;
    }
}
