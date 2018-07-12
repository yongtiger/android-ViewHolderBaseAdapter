package cc.brainbook.adapter.viewholderbaseadapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    ViewHolderBaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mData = getData();

        mAdapter = new ViewHolderBaseAdapter<Map<String, Object>>(mData, R.layout.list_item) {
            @Override
            protected void bindView(ViewHolder holder, final int position) {
                HashMap data = (HashMap) getItem(position);

                TextView title = holder.getView(R.id.tv_title);
                title.setText(data.get("title").toString());

                TextView info = holder.getView(R.id.tv_info);
                info.setText(data.get("info").toString());

                ///test case: image
                ImageView img = holder.getView(R.id.iv_img);
                Object objImg = data.get("img");
                img.setImageResource(Integer.parseInt(objImg.toString()));

                ///test case: click event listener
                Button btn = holder.getView(R.id.btn_view);
//                btn.setTag(position); ///alternative: pass position within tag
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        int position = Integer.parseInt(v.getTag().toString()); ///alternative: pass position within tag

                        ///test case: showInfo(position)
                        showInfo(position);

                        ///test case: remove(position)
//                        mData.remove(position);

                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        ///test case: mAdapter.sort()
        ///https://stackoverflow.com/questions/9906464/sort-listview-with-array-adapter
        mAdapter.sort(new Comparator<HashMap>() {
            @Override
            public int compare(HashMap lhs, HashMap rhs) {
//                return lhs.compareTo(rhs);
                return lhs.get("title").toString().compareTo(rhs.get("title").toString());
            }
        });

        setListAdapter(mAdapter);
    }

    private List getData() {
        final List list = new ArrayList();

        ///demo data for test
        for(int i = 0; i < 100; i++) {
            final HashMap map = new HashMap();
            map.put("title", "G" + i);
            map.put("info", "google " + i);
            if (i % 3 == 0) {
                map.put("img", R.drawable.i1);
            } else if (i % 3 == 1) {
                map.put("img", R.drawable.i2);
            } else if (i % 3 == 2) {
                map.put("img", R.drawable.i3);
            }
            list.add(map);
        }

        return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.v("TAG", mData.get(position).get("title").toString());

        ///test case: showInfo(position)
        showInfo(position);

        ///test case: remove(position)
//        mAdapter.remove(position);

        ///test case: clear()
//        mAdapter.clear();
    }

    public void showInfo(final int position) {
        new AlertDialog.Builder(this)
        .setTitle("Alert Dialog")
        .setMessage("Position: " + position)
        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ///test case: remove(position)
                mAdapter.remove(position);

            }
        })
        .show();
    }

}
