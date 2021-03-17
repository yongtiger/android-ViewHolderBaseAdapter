package cc.brainbook.adapter.viewholderbaseadapter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.brainbook.adapter.viewholderbaseadapter.ViewHolderBaseAdapter;
import cc.brainbook.view.checkableframelayout.CheckableFrameLayout;

public class MainActivity extends Activity {
    private static final String TAG = "TAG";

    private ListView mListView;
    private ViewHolderBaseAdapter<Map<String, Object>> mAdapter;

    private final String[] items = { "按标题正序", "按标题倒序"};
    private int mSortChoice = -1;
    private int mSortCheckedItem = mSortChoice;

    private EditText mEditTextFilterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextFilterText = findViewById(R.id.et_filter_text);

        mListView = findViewById(R.id.lv_list_view);

        resetListView();

        // 排序（sort）按钮的处理
        final Button btnSort = findViewById(R.id.btn_sort);
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder singleChoiceDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                singleChoiceDialog.setTitle("排序");
                singleChoiceDialog.setSingleChoiceItems(items, mSortChoice,  // 第二个参数是默认选项，设置为-1，表示没有任何选项被选中
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

        // 筛选（filter）按钮的处理
        // https://www.androidbegin.com/tutorial/android-search-listview-using-filter/
        mEditTextFilterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                final String constraintString = arg0.toString().trim().toLowerCase(Locale.getDefault());
//                final String constraintString = editTextfilterText.getText().toString().toLowerCase(Locale.getDefault());
                ((Filter) mAdapter.getFilter(new ViewHolderBaseAdapter.FilterCompareCallback<Map<String, Object>>() {
                    @Override
                    public boolean filterCompare(Map<String, Object> object, CharSequence constraint) {
                        return object.get("title").toString().toLowerCase(Locale.getDefault()).indexOf(constraint.toString()) != -1;
                    }
                })).filter(constraintString);

                // 延迟后才能获取准确的getCount()
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 如果存在选中item，则显示批量处理
                        showOrHideBatchSelectAction(mAdapter);
                    }
                }, 50);
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
                if (mEditTextFilterText.getVisibility() == View.GONE) {
                    mEditTextFilterText.setVisibility(View.VISIBLE);
                    // 获得焦点 https://jingyan.baidu.com/article/ceb9fb10c396ae8cad2ba0f7.html
                    mEditTextFilterText.setFocusable(true);mEditTextFilterText.setFocusableInTouchMode(true);mEditTextFilterText.requestFocus();mEditTextFilterText.findFocus();
                    // 打开软键盘 https://www.cnblogs.com/plokmju/p/7978500.html
                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(mEditTextFilterText, 0);
                    }
                } else {
                    mEditTextFilterText.setVisibility(View.GONE);
                    mEditTextFilterText.setText("");
                    // 关闭软键盘 https://www.cnblogs.com/plokmju/p/7978500.html
                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(mEditTextFilterText.getWindowToken(), 0);
                    }
                }
            }
        });

        // 重置（reset）按钮的处理
        final Button btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.reset();

                resetSortAndFilterButtons();
            }
        });

        // 全部选中按钮的处理
        final Button btnAllSelect = findViewById(R.id.all_select);
        btnAllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0, count = mListView.getCount(); i < count; i++) {
                    mListView.setItemChecked(i, true);    // 会被ViewHolderBaseAdapter.bindView()中的mListView.setItemChecked()覆盖！所以只能更改adapter
                    // 更新选中状态（排序后避免错乱！）
                    final HashMap data = (HashMap) mAdapter.getItem(i);
                    data.put("checked", true);
                }
                mAdapter.notifyDataSetChanged();

                // 如果存在选中item，则显示批量处理
                showOrHideBatchSelectAction(mAdapter);
            }
        });

        // 全不选按钮处理
        final Button btnAllUnselect = findViewById(R.id.all_unselect);
        btnAllUnselect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for(int i = 0, count = mListView.getCount(); i < count; i++) {
                    mListView.setItemChecked(i, false);    // 会被ViewHolderBaseAdapter.bindView()中的mListView.setItemChecked()覆盖！所以只能更改adapter
                    // 更新选中状态（排序后避免错乱！）
                    final HashMap data = (HashMap) mAdapter.getItem(i);
                    data.put("checked", false);
                }
                mAdapter.notifyDataSetChanged();

                // 如果存在选中item，则显示批量处理
                showOrHideBatchSelectAction(mAdapter);
            }
        });

        // 反选按钮处理
        final Button btnReverseSelect = findViewById(R.id.reverse_select);
        btnReverseSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0, count = mListView.getCount(); i < count; i++) {
                    mListView.setItemChecked(i, !mListView.isItemChecked(i));    // 会被ViewHolderBaseAdapter.bindView()中的mListView.setItemChecked()覆盖！所以只能更改adapter
                    // 更新选中状态（排序后避免错乱！）
                    final HashMap data = (HashMap) mAdapter.getItem(i);
                    data.put("checked", !(boolean) data.get("checked"));
                }
                mAdapter.notifyDataSetChanged();

                // 如果存在选中item，则显示批量处理
                showOrHideBatchSelectAction(mAdapter);
            }
        });
    }

    private void resetSortAndFilterButtons() {
        Log.d(TAG, "resetSortAndFilterButtons: ");

        mSortChoice = -1;
        mSortCheckedItem = -1;

        if (mEditTextFilterText.getVisibility() != View.GONE) {
            mEditTextFilterText.setVisibility(View.GONE);
            mEditTextFilterText.setText("");
            // 关闭软键盘 https://www.cnblogs.com/plokmju/p/7978500.html
            final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(mEditTextFilterText.getWindowToken(), 0);
            }
        }
    }

    @NonNull
    private ArrayList<Long> getCheckedItemIds(@NonNull Adapter adapter) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < adapter.getCount(); i++){
            final HashMap data = (HashMap) adapter.getItem(i);
            final boolean checked = (boolean) data.get("checked");
            if (checked) {
                final int checkedItemId = (int) data.get("id");
                list.add(checkedItemId);
            }
        }
        return list;
    }

    @NonNull
    private ArrayList getCheckedItems(@NonNull Adapter adapter) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < adapter.getCount(); i++){
            final HashMap data = (HashMap) adapter.getItem(i);
            final boolean checked = (boolean) data.get("checked");
            if (checked) {
                list.add(data);
            }
        }
        return list;
    }

    private void removeCheckedItems(@NonNull ViewHolderBaseAdapter adapter) {
        for (int i = adapter.getCount() - 1; i >= 0 ; i--){
            final HashMap data = (HashMap) adapter.getItem(i);
            final boolean checked = (boolean) data.get("checked");
            if (checked) {
                adapter.remove(data);
            }
        }
    }

    private void showOrHideBatchSelectAction(@NonNull Adapter adapter) {
        // 如果存在选中item，则显示批量处理
        final LinearLayout linearLayoutBatchSelectAction = findViewById(R.id.ll_batch_select_action);
        final int checkedItemIdsCount = getCheckedItemIds(adapter).size();
        if (checkedItemIdsCount > 0) {
            Button btnBatchSelectActionDelete = findViewById(R.id.batch_select_action_delete);
            btnBatchSelectActionDelete.setText("批量删除(" + checkedItemIdsCount +")");
            linearLayoutBatchSelectAction.setVisibility(View.VISIBLE);
        } else {
            linearLayoutBatchSelectAction.setVisibility(View.GONE);
        }
    }

    public void clickBatchSelectActionDelete(View view) {
        final ArrayList<Long> checkedItemIds = getCheckedItemIds(mAdapter);
        Log.d(TAG, "clickBatchSelectActionDelete: checkedItemIds: " + checkedItemIds);

        if (!checkedItemIds.isEmpty()) {
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage("确定批量删除(" + checkedItemIds.size() + ")条记录吗？")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 数据库记录删除操作
                            // todo ...

                            // mAdapter批量删除
                            removeCheckedItems(mAdapter);

                            // 如果存在选中item，则显示批量处理
                            showOrHideBatchSelectAction(mAdapter);

                            Toast.makeText(getApplicationContext(), "批量删除了(" + checkedItemIds.size() + ")条记录", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    @NonNull
    private List getData() {
        final List list = new ArrayList();

        ///demo data for test
        for(int i = 0; i < 1000; i++) {
            final HashMap map = new HashMap();
            map.put("id", i);
            map.put("checked", false);
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

    private void resetListView() {
        Log.d(TAG, "resetListView: ");

        final List dataList = getData();
        mAdapter = new ViewHolderBaseAdapter<Map<String, Object>>(dataList, R.layout.list_item) {
            @Override
            protected void bindView(ViewHolder holder, final int position) {
                HashMap data = (HashMap) getItem(position);

                // 更新选中状态（排序后避免错乱！）
//                final CheckableFrameLayout checkableFrameLayout = holder.getView(R.id.cfl_checkable_frame_layout);
//                checkableFrameLayout.setChecked((boolean) data.get("checked"));   // 无效！
//                final CheckBox checkBox = holder.getView(R.id.cb_check_box);
////                checkBox.setChecked((boolean) data.get("checked"));   // 无效！
                mListView.setItemChecked(position, (boolean) data.get("checked"));

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
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Click ... " + position, Toast.LENGTH_SHORT).show();

                //        Log.d(TAG, (String)mData.get(position).get("title"));
                Log.d(TAG, "onClick getCheckedItemCount(): " + mListView.getCheckedItemCount());

//                int selectPosition = getListView().getCheckedItemPosition();///单选模式
//                Log.d(TAG, "onClick selectPosition: "+selectPosition+", " + getListView().getAdapter().getItem(selectPosition));

                final SparseBooleanArray selectPositions = mListView.getCheckedItemPositions();
                Log.d(TAG, "onClick selectPositions: "+selectPositions);

                // 更新选中状态（排序后避免错乱！）
                final CheckableFrameLayout checkableFrameLayout = view.findViewById(R.id.cfl_checkable_frame_layout);
                final HashMap data = (HashMap) mAdapter.getItem(position);
                data.put("checked", checkableFrameLayout.isChecked());

                // 如果存在选中item，则显示批量处理
                showOrHideBatchSelectAction(mAdapter);
            }
        });

        resetSortAndFilterButtons();

        // 如果存在选中item，则显示批量处理
        showOrHideBatchSelectAction(mAdapter);
    }

    private void sort(final int sortMode) {
        ///https://stackoverflow.com/questions/9906464/sort-listview-with-array-adapter
        mAdapter.sort(new Comparator<Map>() {
            @Override
            public int compare(Map lhs, Map rhs) {
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
