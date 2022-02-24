package com.e_co.weddingprovider.uis.activity_home.fragments_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.adapter.OfferAdapter;
import com.e_co.weddingprovider.adapter.SliderAdapter;
import com.e_co.weddingprovider.databinding.FragmentServiceDetailsBinding;
import com.e_co.weddingprovider.model.ServiceModel;
import com.e_co.weddingprovider.model.SingleServiceDataModel;
import com.e_co.weddingprovider.mvvm.FragmentServiceDetialsMvvm;
import com.e_co.weddingprovider.uis.activity_add_offer.AddOfferActivity;
import com.e_co.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.e_co.weddingprovider.uis.activity_base.BaseFragment;
import com.e_co.weddingprovider.uis.activity_home.HomeActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ServiceDetailsFragment extends BaseFragment {
    private static final String TAG = ServiceDetailsFragment.class.getName();
    private FragmentServiceDetialsMvvm fragmentServiceDetialsMvvm;
    private HomeActivity activity;
    private FragmentServiceDetailsBinding binding;
    private CompositeDisposable disposable = new CompositeDisposable();
    private boolean isInFullScreen = false;
    private Timer timer;
    private TimerTask timerTask;
    private SliderAdapter sliderAdapter;
    private SingleServiceDataModel singleServiceDataModel;
    private String service_id = "";
    private ActivityResultLauncher<Intent> launcher;
    private int req;
    private OfferAdapter offerAdapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req == 1 && result.getResultCode() == Activity.RESULT_OK) {

                fragmentServiceDetialsMvvm.getSingleServiceData(service_id);

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            service_id = bundle.getString("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_details, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Observable.timer(130, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        initView();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private void initView() {
        fragmentServiceDetialsMvvm = ViewModelProviders.of(this).get(FragmentServiceDetialsMvvm.class);
        fragmentServiceDetialsMvvm.getIsLoading().observe(activity, isLoading -> {
            if (isLoading) {
                binding.progBar.setVisibility(View.VISIBLE);
                binding.nested.setVisibility(View.GONE);

            } else {
                binding.progBar.setVisibility(View.GONE);
                binding.nested.setVisibility(View.VISIBLE);
            }

        });

        fragmentServiceDetialsMvvm.onDeleteSuccess().observe(activity, integer -> {
            if (fragmentServiceDetialsMvvm.getSingleService().getValue().getData().getOffer().size() > 0) {
                fragmentServiceDetialsMvvm.getSingleService().getValue().getData().getOffer().remove(integer);
                if (offerAdapter != null) {
                    offerAdapter.removeItem(integer);
                }

            }
        });
        binding.recView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recView);

        binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                binding.progBarVideo.setVisibility(View.GONE);

            }


        });


        fragmentServiceDetialsMvvm.getSingleService().observe(activity, s -> {
            singleServiceDataModel = s;
            if (singleServiceDataModel != null && singleServiceDataModel.getData() != null && singleServiceDataModel.getData().getOffer().size() > 0) {
                offerAdapter = new OfferAdapter(activity, this);

                binding.recView.setAdapter(offerAdapter);
                offerAdapter.updateList(singleServiceDataModel.getData().getOffer());

            }
            binding.setModel(singleServiceDataModel.getData());
            if (singleServiceDataModel != null && singleServiceDataModel.getData() != null) {
                if (singleServiceDataModel.getData().getService_images() != null && singleServiceDataModel.getData().getService_images().size() > 0) {
                    sliderAdapter = new SliderAdapter(singleServiceDataModel.getData().getService_images(), activity);
                    binding.pager.setAdapter(sliderAdapter);
                    binding.tab.setupWithViewPager(binding.pager);
                    if (singleServiceDataModel.getData().getService_images().size() > 1) {
                        timer = new Timer();
                        timerTask = new MyTask();
                        timer.scheduleAtFixedRate(timerTask, 6000, 6000);
                    }
                }
                if (singleServiceDataModel.getData().getVideo_link() != null) {

                    binding.flVideo.setVisibility(View.VISIBLE);
                    binding.webView.loadUrl(singleServiceDataModel.getData().getVideo_link());
                } else {
                    binding.flVideo.setVisibility(View.GONE);
                }


            }

            Log.e("vide", singleServiceDataModel.getData().getVideo_link() + "__");
        });
        fragmentServiceDetialsMvvm.getSingleServiceData(service_id);


        binding.btnUpdate.setOnClickListener(v -> {
            req = 1;
            Intent intent = new Intent(activity, AddServiceActivity.class);
            intent.putExtra("data", singleServiceDataModel.getData());
            launcher.launch(intent);

        });

        binding.btnOffer.setOnClickListener(v -> {
            req = 1;
            Intent intent = new Intent(activity, AddOfferActivity.class);
            intent.putExtra("data", singleServiceDataModel.getData());
            launcher.launch(intent);

        });


    }


    public void updateOffer(ServiceModel.OfferModel offerModel) {
        req = 1;
        Intent intent = new Intent(activity, AddOfferActivity.class);
        intent.putExtra("data", singleServiceDataModel.getData());
        intent.putExtra("data2", offerModel);

        launcher.launch(intent);
    }

    public void deleteOffer(String id, int adapterPosition) {
        fragmentServiceDetialsMvvm.deleteOfferData(getUserModel(), id, adapterPosition, activity);
    }


    public class MyTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(() -> {
                int current_page = binding.pager.getCurrentItem();
                if (current_page < sliderAdapter.getCount() - 1) {
                    binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
                } else {
                    binding.pager.setCurrentItem(0);

                }
            });

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        binding.webView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }


        disposable.clear();


    }
}