package com.projects.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.application.MainApplication;
import com.config.Config;
import com.db.Queries;
import com.libraries.adapters.MGRecyclerAdapter;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.libraries.dataparser.DataParser;
import com.libraries.utilities.MGUtilities;
import com.models.Category;
import com.models.Data;
import com.projects.activities.RestaurantActivity;
import com.apps.gasfinder.R;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private View viewInflate;
    ArrayList<Category> categories;
    MGAsyncTaskNoDialog task;
    Queries q;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public CategoryFragment() { }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_list_swipe, null);
        return viewInflate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        categories = new ArrayList<Category>();
        q = MainApplication.getQueriesInstance(getContext());

        mRecyclerView = (RecyclerView) viewInflate.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefresh = (SwipeRefreshLayout) viewInflate.findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        showRefresh(false);
        getData();
    }

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    public void getData() {
        task = new MGAsyncTaskNoDialog(getActivity());
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                if(!MGUtilities.hasConnection(getActivity())) {
                    categories = q.getCategories();
                }
                showList();
                showRefresh(false);
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                categories = q.getCategories();
                if( MGUtilities.hasConnection(getActivity()) && categories.size() == 0) {
                    try {
                        String strUrl = "";
                        strUrl = String.format("%s?api_key=%s&get_categories=1",
                                Config.GET_DATA_URL,
                                Config.API_KEY);

                        Log.e("URL", strUrl);
                        DataParser parser = new DataParser();
                        Data data = parser.getData(strUrl);
                        if (data == null)
                            return;

                        if (data.getCategories() != null && data.getCategories().size() > 0) {
                            for(Category category : data.getCategories()) {
                                q.deleteCategory(category.getCategory_id());
                                q.insertCategory(category);
                                categories.add(category);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        task.execute();
    }

    @Override
    public void onDestroyView()  {
        super.onDestroyView();
        if (viewInflate != null) {
            ViewGroup parentViewGroup = (ViewGroup) viewInflate.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }

        if(task != null)
            task.cancel(true);
    }

    private void showList() {
        MGRecyclerAdapter adapter = new MGRecyclerAdapter(categories.size(), R.layout.category_entry);
        adapter.setOnMGRecyclerAdapterListener(new MGRecyclerAdapter.OnMGRecyclerAdapterListener() {

            @Override
            public void onMGRecyclerAdapterCreated(MGRecyclerAdapter adapter, MGRecyclerAdapter.ViewHolder v, int position) {
                final Category category = categories.get(position);
                Spanned name = Html.fromHtml(category.getCategory());
                TextView tvTitle = (TextView) v.view.findViewById(R.id.tvTitle);
                tvTitle.setText(name);

                FrameLayout frameCategory = (FrameLayout) v.view.findViewById(R.id.frameCategory);
                frameCategory.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), RestaurantActivity.class);
                        i.putExtra("category", category);
                        startActivity(i);
                    }
                });
            }

        });
        mRecyclerView.setAdapter(adapter);
    }
}
