package me.dawson.listgrid.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListGrid extends ListView {
	public static final String TAG = "ListGrid";

	private int rowCount;
	private ListAdapter mockAdapter;
	private BaseAdapter realAdapter;
	private OnItemClickListener itemClickListener;

	class InnerAdapter extends BaseAdapter implements OnClickListener {

		private int realCount;

		public InnerAdapter() {
			realCount = 0;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			if (mockAdapter == null) {
				return 0;
			}
			int count = 0;
			realCount = mockAdapter.getCount();
			if (rowCount <= 0) {
				count = realCount;
			} else {
				double v = ((double) realCount) / rowCount;
				count = (int) Math.ceil(v);
			}
			Log.d(TAG, "getCount count " + count);
			return count;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mockAdapter == null) {
				return null;
			}

			Log.d(TAG, "getView position " + position);
			// get container linear layout
			LinearLayout container = null;
			if (!(convertView instanceof LinearLayout)) {
				container = new LinearLayout(parent.getContext());
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				container.setLayoutParams(lp);
				container.setOrientation(LinearLayout.HORIZONTAL);
			} else {
				container = (LinearLayout) convertView;
			}

			// get children views
			List<View> recycleViews = new LinkedList<View>();
			int childCount = container.getChildCount();
			for (int index = 0; index < childCount; ++index) {
				recycleViews.add(container.getChildAt(index));
			}
			container.removeAllViews();

			int containerCount = rowCount;
			if (position == (getCount() - 1)) {
				containerCount = realCount % rowCount;
			}

			for (int index = 0; index < containerCount; ++index) {
				int realPos = position * rowCount + index;
				View recycleView = null;
				if (recycleViews.size() > 0) {
					recycleView = recycleViews.remove(0);
				}
				View childView = mockAdapter.getView(realPos, recycleView,
						container);
				int width = ListGrid.this.getWidth() / rowCount;
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						width, LinearLayout.LayoutParams.MATCH_PARENT);
				childView.setOnClickListener(this);
				childView.setTag(realPos);
				// lp.weight = 1.0f;
				container.addView(childView, index, lp);
			}

			return container;
		}

		@Override
		public void onClick(View v) {
			int realPos = (Integer) v.getTag();
			if (itemClickListener != null) {
				itemClickListener.onItemClick(ListGrid.this, v, realPos,
						v.getId());
			}
		}
	};

	public ListGrid(Context context) {
		super(context);
		init();
	}

	public ListGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ListGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		rowCount = 1;
		realAdapter = new InnerAdapter();
		super.setAdapter(realAdapter);
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		this.mockAdapter = adapter;
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		itemClickListener = listener;
	}
}