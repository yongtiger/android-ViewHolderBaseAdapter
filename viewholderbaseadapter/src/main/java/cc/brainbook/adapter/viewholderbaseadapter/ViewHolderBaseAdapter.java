package cc.brainbook.adapter.viewholderbaseadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description.
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2018/7/5 18:20
 */
///https://blog.csdn.net/lisdye2/article/details/51449707
///https://blog.csdn.net/lpCrazyBoy/article/details/80732517
public abstract  class ViewHolderBaseAdapter<T> extends BaseAdapter {

    private List<T> mData;
    private int mLayoutRes;

    public ViewHolderBaseAdapter(List<T> data, int layoutRes) {
        this.mData = data;
        this.mLayoutRes = layoutRes;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(mLayoutRes, parent,false);
            vHolder = new ViewHolder(convertView);
            convertView.setTag(vHolder);
        }else{
            vHolder = (ViewHolder) convertView.getTag();
        }
        bindView(vHolder, position);
        return convertView;
    }

    protected abstract void bindView(ViewHolder vHolder,  int position);

    /**
     * Adds the specified object at the end of the list.
     *
     * @param object The object to add at the end of the list.
     */
    public void add(@Nullable T object) {
        mData.add(object);
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the list.
     *
     * @param collection The Collection to add at the end of the list.
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this list
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this list does not permit null
     *         elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this list
     */
    public void addAll(@NonNull Collection<? extends T> collection) {
        mData.addAll(collection);
        notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the list.
     *
     * @param items The items to add at the end of the list.
     */
    public void addAll(T ... items) {
        Collections.addAll(mData, items);
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the list.
     *
     * @param object The object to insert into the list.
     * @param index The index at which the object must be inserted.
     */
    public void insert(@Nullable T object, int index) {
        mData.add(index, object);
        notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the list.
     *
     * @param object The object to remove.
     */
    public void remove(@Nullable T object) {
        if (mData != null) {
            mData.remove(object);
            notifyDataSetChanged();
        }
    }

    /**
     * Removes the specified index from the list.
     *
     * @param index The index to remove.
     */
    public void remove(int index) {
        if (mData != null) {
            mData.remove(index);
            notifyDataSetChanged();
        }
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        Collections.sort(mData, comparator);
        notifyDataSetChanged();
    }

    /**
     * Returns the position of the specified item in the list.
     *
     * @param item The item to retrieve the position of.
     *
     * @return The position of the specified item.
     */
    public int getPosition(@Nullable T item) {
        return mData.indexOf(item);
    }


    /**
     * Class ViewHolder
     */
    public static class ViewHolder {

        private View mConvertView;
        private SparseArray<View> mViews;   ///https://blog.csdn.net/lpCrazyBoy/article/details/80732517

        public ViewHolder(View convertView) {
            this.mConvertView = convertView;
            mViews = new SparseArray<>();
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int resId) {
            T view = (T) mViews.get(resId);
            if (view == null) {
                view = mConvertView.findViewById(resId);
                mViews.put(resId, view);
            }
            return view;
        }
    }
}