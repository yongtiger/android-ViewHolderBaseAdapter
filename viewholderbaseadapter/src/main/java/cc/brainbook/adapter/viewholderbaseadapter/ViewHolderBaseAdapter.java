package cc.brainbook.adapter.viewholderbaseadapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class ViewHolderBaseAdapter
 *
 * @author Robert Han
 * @email brainbook.cc@outlook.com
 * @website www.brainbook.cc
 * @time 2018/7/5 18:20
 */
public abstract  class ViewHolderBaseAdapter<T> extends BaseAdapter implements Filterable {
    private List<T> mObjects;
    private int mLayoutRes;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the list should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original list of data.
     */
    private final Object mLock = new Object();

    // A copy of the original mObjects list, initialized from and then used instead as soon as
    // the mFilter ListFilter is used. mObjects will then only contain the filtered values.
    private List<T> mOriginalValues;

    public ViewHolderBaseAdapter(List<T> data, int layoutRes) {
        this.mObjects = data;
        this.mLayoutRes = layoutRes;
    }

    @Override
    public int getCount() {
        return mObjects == null ? 0 : mObjects.size();
    }

    @Override
    public T getItem(int position) {
        return mObjects.get(position);
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
        synchronized (mLock) {
            mObjects.add(object);
        }
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
        synchronized (mLock) {
            mObjects.addAll(collection);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the list.
     *
     * @param items The items to add at the end of the list.
     */
    public void addAll(T ... items) {
        synchronized (mLock) {
            Collections.addAll(mObjects, items);
        }
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the list.
     *
     * @param object The object to insert into the list.
     * @param index The index at which the object must be inserted.
     */
    public void insert(@Nullable T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the list.
     *
     * @param object The object to remove.
     */
    public void remove(@Nullable T object) {
        synchronized (mLock) {
            mObjects.remove(object);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified index from the list.
     *
     * @param index The index to remove.
     */
    public void remove(int index) {
        synchronized (mLock) {
            mObjects.remove(index);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * Resets all elements from the original list.
     */
    public void reset() {
        if (mNewValues != null) {
            synchronized (mLock) {
                mObjects = new ArrayList<T>(mNewValues);
            }
        } else if (mOriginalValues != null) {
            synchronized (mLock) {
                mObjects = new ArrayList<T>(mOriginalValues);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        if (mOriginalValues == null) {
            synchronized (mLock) {
                mOriginalValues = new ArrayList<T>(mObjects);
            }
        }

        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
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
        return mObjects.indexOf(item);
    }

    /**
     * Class ViewHolder
     */
    public static class ViewHolder {

        private View mConvertView;
        private SparseArray<View> mViews;

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


    /* -------------------------------- Customize filter -------------------------------- */
    // https://www.jb51.net/article/109480.htm
    private ListFilter mFilter;
    private ArrayList<T> mNewValues;

    public Filter getFilter(FilterCompareCallback filterCompareCallback) {
        mFilter = (ListFilter) getFilter();
        mFilter.setFilterCompareCallback(filterCompareCallback);
        return mFilter;
    }
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ListFilter();
        }
        return mFilter;
    }

    /**
     * <p>An list filter constrains the content of the base adapter with
     * a string. It will compare each item with FilterCompareCallback.filterCompare(),
     * and remove from the list that does not match the supplied string.</p>
     *
     * https://stackoverflow.com/questions/14663725/list-view-filter-android
     */
    private class ListFilter extends Filter {
        private FilterCompareCallback mFilterCompareCallback;
        public void setFilterCompareCallback(FilterCompareCallback filterCompareCallback) {
            mFilterCompareCallback = filterCompareCallback;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<T>(mObjects);
                }
            }

            if (constraint == null || constraint.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<T>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<T>(mOriginalValues);
                }

                final int count = values.size();
                mNewValues = new ArrayList<T>();

                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);

                    if (mFilterCompareCallback.filterCompare(value, constraint)) {
                        mNewValues.add(value);
                    }
                }

                results.values = mNewValues;
                results.count = mNewValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when a filter is used in comparing.
     */
    public interface FilterCompareCallback<T> {
        boolean filterCompare(@Nullable T object, CharSequence constraint);
    }
}