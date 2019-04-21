package com.RecyList.android.view;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.RecyList.android.R;
import com.RecyList.android.di.component.CountryActivityComponent;
import com.RecyList.android.di.module.CountryActivityModule;
import com.RecyList.android.model.remote.Resource;
import com.RecyList.android.model.repository.DisplayData;
import com.RecyList.android.util.UtilBundle;
import com.RecyList.android.viewmodel.RecyListDataViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.INVISIBLE;
import static com.RecyList.android.model.data.InternalDataConfiguration.INTENT_RQ_CODE;
import static com.RecyList.android.model.remote.Resource.Status.ERROR;
import static com.RecyList.android.model.remote.Resource.Status.LOADING;
import static com.RecyList.android.model.remote.Resource.Status.SUCCESS;

public class CountryActivity extends BaseActivity {

    private CountryActivityComponent mComponent = null;
    private ArrayList<ItemRecyclerDisplayData> mItemList = new ArrayList<>();
    private RecyListDataViewModel mViewModel = null;
    private RecyclerAdapter mAdapter = null;
    private RecyclerView mRecyclerView = null;
    private IndexBarView mIndexBarView = null;
    private TextView mIndexBarText = null;
    private TextView mLoadFailedView = null;
    private Group mLoadingGroup = null;
    private TitleDecoration mTitle = null;
    private Context mCtx = null;

    public static void start(@NonNull Activity start, Context ctx) {
        Intent intent = new Intent(ctx, CountryActivity.class);
        start.startActivityForResult(intent, INTENT_RQ_CODE, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        initInjector();
        initViewModel();
        initView();
    }

    @Inject
    public void setContext(CountryActivity activity) {
        mCtx = activity;//mCtx = this;
    }

    private void initInjector() {
        mComponent = getApplicationComponent()
                .countryActivityComponent()
                .countryActivityModule(new CountryActivityModule(this))
                .build();
        mComponent.inject(this);
    }

    public CountryActivityComponent getCountryActivityComponent() {
        return mComponent;
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(CountryActivity.this).get(RecyListDataViewModel.class);
        mViewModel.getLiveDataAllDisplayData().observe(this, observerAllData);
        mViewModel.getDataByFilter().observe(this, observerFilterData);
    }

    private void initView() {
        buildToolBarView();
        buildRecyclerView();
        buildIndexBarView();
        buildLoadingView();
    }

    private void buildToolBarView() {

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mCtx));

        mAdapter = new RecyclerAdapter(mCtx);
        mAdapter.setItemClickListener(itemListener);
        mRecyclerView.setAdapter(mAdapter);

        mTitle = new TitleDecoration(mCtx);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(mTitle);
    }

    private void buildIndexBarView() {
        mIndexBarView = findViewById(R.id.index_bar);
        mIndexBarText = findViewById(R.id.index_bar_text);
        mIndexBarText.setVisibility(INVISIBLE);//Default

        mIndexBarView.setHintText(mIndexBarText);
        mIndexBarView.setOnTouchEventListener(indexListener);
    }

    private void buildLoadingView() {
        mLoadFailedView = findViewById(R.id.load_failInfo);
        mLoadingGroup = findViewById(R.id.group_loading);

        mLoadFailedView.setVisibility(INVISIBLE);//Default
    }

    private IndexBarView.OnTouchEventListener indexListener = new IndexBarView.OnTouchEventListener() {
        @Override
        public void onTouchListener(String it) {

            int position = mAdapter.getPositionByIndex(it);
            if (position != RecyclerView.NO_POSITION) {
                //mRecyclerView.scrollToPosition(position);
                ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
            }
        }
    };

    private RecyclerAdapter.OnItemClickListener itemListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (position != RecyclerView.NO_POSITION) {
                encodeSelectItemToIntent(mItemList.get(position));
                finish();
            }
        }
    };

    private void encodeSelectItemToIntent(ItemRecyclerDisplayData data) {

        Intent bIntent = new Intent();
        setIntentPara(bIntent, data.getAlpha2Code(), data.getCallCode());

        setResult(RESULT_OK, bIntent);
    }

    private void setIntentPara(Intent intent, String flagId, String callId) {

        Bundle bundle = new Bundle();
        UtilBundle.addDataToBundle(bundle, flagId, callId);
        intent.putExtras(bundle);
    }

    private Observer<Resource<List<DisplayData>>> observerAllData = new Observer<Resource<List<DisplayData>>>() {
        @Override
        public void onChanged(@Nullable Resource<List<DisplayData>> listResource) {
            //actually, it only invoked one time, because the data never changed, unless add new country/region
            //if (mItemList.size() != 0) {return;}//just display without any logical
            updateViewStatus(listResource.mStatus);
            if (listResource.mStatus == SUCCESS) {
                updateViewData(listResource.mData);
            }
        }
    };

    private void updateViewStatus(Resource.Status status) {
        mLoadingGroup.setVisibility((status == LOADING) ? View.VISIBLE : View.INVISIBLE);
        mLoadFailedView.setVisibility((status == ERROR) ? View.VISIBLE : View.INVISIBLE);
        mRecyclerView.setVisibility((status == SUCCESS) ? View.VISIBLE : View.INVISIBLE);
        //mIndexBarText is decided by ACTION UP DOWN or MOVE
        mIndexBarView.setVisibility((status == SUCCESS) ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateViewData(List<DisplayData> listData) {
        if ((listData != null) && (listData.size() > 0)) {
            prepareItemListData(listData);
            mAdapter.setData(mItemList);
            if (mTitle != null) {
                mTitle.setData(mItemList);
            }
        }
    }

    private Observer<List<DisplayData>> observerFilterData = new Observer<List<DisplayData>>() {
        @Override
        public void onChanged(@Nullable List<DisplayData> listResource) {
            if ((listResource != null) && (listResource.size() > 0)) {
                prepareItemListData(listResource);
                mAdapter.setData(mItemList);
            }
        }
    };

    private void prepareItemListData(List<DisplayData> it) {

        DisplayData tmp = null;
        mItemList.clear();
        for (int i = 0; i < it.size(); i++) {
            tmp = it.get(i);
            mItemList.add(new ItemRecyclerDisplayData(tmp.alpha2Code, tmp.name, tmp.callCode));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) (searchItem.getActionView());

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(searchListener);
        //searchView.setQueryHint(DEFAULT_SEARCH_VIEW_HINT);
        //searchView.setIconifiedByDefault(false);//Icon always display
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean result = true;//default is true
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            //Toast.makeText(mCtx, "Start to Search " + s, Toast.LENGTH_SHORT).show();
            mViewModel.setFilter(s);
            return false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}