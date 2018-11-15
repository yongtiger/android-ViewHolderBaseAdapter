package cc.brainbook.adapter.viewholderbaseadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private ListView mList;
    private List<Map<String, Object>> mData;
    private ViewHolderBaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mList = findViewById(R.id.lv_list_view);

        ///test case: CHOICE_MODE_SINGLE
//        mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


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
                        Log.d("TAG", "btn.onClick: ");
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
                return rhs.get("title").toString().compareTo(lhs.get("title").toString());
            }
        });
        mAdapter.reset();


        ///test case: mAdapter.getFilter()
//        mAdapter.getFilter(new ViewHolderBaseAdapter.FilterCompareCallback<HashMap<String, Object>>() {
//            @Override
//            public boolean filterCompare(HashMap<String, Object> object, CharSequence constraint) {
//                return object.get("title").toString().toLowerCase().indexOf(constraint.toString().toLowerCase()) != -1;
//            }
//        }).filter("G2");


        ///test case: Capture Text in EditText, and then mAdapter.getFilter()
        // https://www.androidbegin.com/tutorial/android-search-listview-using-filter/
        final EditText editText = findViewById(R.id.et_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                final String constraintString = arg0.toString().trim().toLowerCase(Locale.getDefault());
//                final String constraintString = editText.getText().toString().trim().toLowerCase(Locale.getDefault());
                mAdapter.getFilter(new ViewHolderBaseAdapter.FilterCompareCallback<HashMap<String, Object>>() {
                    @Override
                    public boolean filterCompare(HashMap<String, Object> object, CharSequence constraint) {
                        return object.get("title").toString().toLowerCase(Locale.getDefault()).indexOf(constraint.toString()) != -1;
                    }
                }).filter(constraintString);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });


        mList.setAdapter(mAdapter);


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("TAG", "onItemClick: ");

                ///test case: showInfo(position)
                showInfo(position);

                ///test case: remove(position)
//                mAdapter.remove(position);

                ///test case: clear()
//                mAdapter.clear();
            }
        });
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
