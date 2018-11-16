package cc.brainbook.adapter.viewholderbaseadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "TAG";

    private ListView mListView;
    private List<Map<String, Object>> mData;
    private ViewHolderBaseAdapter mAdapter;

    private final String[] items = { "按标题正序", "按标题倒序"};
    private int mSortChoice = -1;
    private int mSortCheckedItem = mSortChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        mListView = findViewById(R.id.lv_list_view);

        ///test case: CHOICE_MODE_SINGLE
//        mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Click ... " + position, Toast.LENGTH_SHORT).show();

                //        Log.d(TAG, (String)mData.get(position).get("title"));
                Log.d(TAG, "onClick getCheckedItemCount(): " + mListView.getCheckedItemCount());

//                int selectPosition = getListView().getCheckedItemPosition();///单选模式
//                Log.d(TAG, "onClick selectPosition: "+selectPosition+", " + getListView().getAdapter().getItem(selectPosition));

                SparseBooleanArray selectPositions = mListView.getCheckedItemPositions();
                Log.d(TAG, "onClick selectPositions: "+selectPositions);

                // 更新选中状态（排序后避免错乱！）
//                final CheckableFrameLayout checkableFrameLayout = view.findViewById(R.id.cfl_checkable_frame_layout);
//                final HashMap data = (HashMap) mAdapter.getItem(position);
//                data.put("checked", checkableFrameLayout.isChecked());
            }
        });


        //全部选中按钮的处理
        final Button all_sel = findViewById(R.id.all_sel);
        all_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0, count = mAdapter.getCount(); i < count; i++) {
                    mListView.setItemChecked(i, true);
                }
            }
        });

        //全部取消按钮处理
        final Button all_unsel = findViewById(R.id.all_unsel);
        all_unsel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for(int i = 0, count = mAdapter.getCount(); i < count; i++) {
                    mListView.setItemChecked(i, false);
                }
            }
        });

        //反选按钮处理
        final Button reverse_sel = findViewById(R.id.reverse_sel);
        reverse_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0, count = mAdapter.getCount(); i < count; i++) {
                    mListView.setItemChecked(i, !mListView.isItemChecked(i));
                }
            }
        });

        // 排序（sort）
        final Button btnSort = findViewById(R.id.btn_sort);
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder singleChoiceDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                singleChoiceDialog.setTitle("排序");
                singleChoiceDialog.setSingleChoiceItems(items, mSortChoice,  // 第二个参数是默认选项，此处设置为-1，表示没有任何选项被选中
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: 选中: which: " + which);
                                mSortCheckedItem = which;
                            }
                        });
                singleChoiceDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: 确定: which: " + which);
                                if (mSortCheckedItem >= 0 && mSortCheckedItem < items.length) {
                                    mSortChoice = mSortCheckedItem;
                                    sort(mSortChoice);
                                }
                            }
                        });
                singleChoiceDialog.setNegativeButton("取消", null);
                singleChoiceDialog.setNeutralButton("取消排序",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: 取消排序: which: " + which);
                                mSortChoice = -1;
                                mSortCheckedItem = -1;

                                // 不排序
                                mAdapter.sort(null);
                                // https://stackoverflow.com/questions/9647507/how-to-reset-the-listview-with-the-arrayadapter-after-fetching-data
//                                Log.d(TAG, "onClick: dataList" + dataList.size());
//                                mAdapter.clear();   // clear之后dataList变为空！
//                                Log.d(TAG, "onClick: dataList" + dataList.size());
//                                final List dataList = getData(mQueryList);
//                                mAdapter.addAll(dataList);
                            }
                        });
                singleChoiceDialog.show();
            }
        });


        // 筛选（filter）
        ///test case: Capture Text in EditText, and then mAdapter.getFilter()
        // https://www.androidbegin.com/tutorial/android-search-listview-using-filter/
        final EditText editTextfilterText = findViewById(R.id.et_filter_text);
        editTextfilterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                final String constraintString = arg0.toString().trim().toLowerCase(Locale.getDefault());
//                final String constraintString = editTextfilterText.getText().toString().toLowerCase(Locale.getDefault());
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
        final Button btnFilter = findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextfilterText.getVisibility() == View.GONE) {
                    editTextfilterText.setVisibility(View.VISIBLE);
                    // 获得焦点 https://jingyan.baidu.com/article/ceb9fb10c396ae8cad2ba0f7.html
                    editTextfilterText.setFocusable(true);editTextfilterText.setFocusableInTouchMode(true);editTextfilterText.requestFocus();editTextfilterText.findFocus();
                    // 打开软键盘 https://www.cnblogs.com/plokmju/p/7978500.html
                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(editTextfilterText, 0);
                    }
                } else {
                    editTextfilterText.setVisibility(View.GONE);
                    editTextfilterText.setText("");
                    // 关闭软键盘 https://www.cnblogs.com/plokmju/p/7978500.html
                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(editTextfilterText.getWindowToken(), 0);
                    }
                }
            }
        });

        final Button btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.reset();

                mSortChoice = -1;
                mSortCheckedItem = -1;

                if (editTextfilterText.getVisibility() != View.GONE) {
                    editTextfilterText.setVisibility(View.GONE);
                    editTextfilterText.setText("");
                    // 关闭软键盘 https://www.cnblogs.com/plokmju/p/7978500.html
                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(editTextfilterText.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    private List getData() {
        final List list = new ArrayList();

        ///demo data for test
        for(int i = 0; i < 1000; i++) {
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

    private void sort(final int sortMode) {
        ///https://stackoverflow.com/questions/9906464/sort-listview-with-array-adapter
        mAdapter.sort(new Comparator<HashMap>() {
            @Override
            public int compare(HashMap lhs, HashMap rhs) {
                if (sortMode == 0) {
                    return lhs.get("title").toString()
                            .compareTo(rhs.get("title").toString());
                } else {
                    return rhs.get("title").toString()
                            .compareTo(lhs.get("title").toString());
                }
            }
        });
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
