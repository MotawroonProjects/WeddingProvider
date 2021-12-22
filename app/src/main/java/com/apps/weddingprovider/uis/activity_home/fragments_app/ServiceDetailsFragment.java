package com.apps.weddingprovider.uis.activity_home.fragments_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.adapter.SliderAdapter;
import com.apps.weddingprovider.databinding.FragmentServiceDetailsBinding;
import com.apps.weddingprovider.model.SingleServiceDataModel;
import com.apps.weddingprovider.mvvm.FragmentServiceDetialsMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseFragment;
import com.apps.weddingprovider.uis.activity_home.HomeActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

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
    private ExoPlayer player;
    private DataSource.Factory dataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private CompositeDisposable disposable = new CompositeDisposable();
    private boolean isInFullScreen = false;
    private Timer timer;
    private TimerTask timerTask;
    private SliderAdapter sliderAdapter;
    private SingleServiceDataModel singleServiceDataModel;
    private String service_id = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
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

        fragmentServiceDetialsMvvm.getSingleService().observe(activity, s -> {
            singleServiceDataModel = s;
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
                if (singleServiceDataModel.getData().getVideo() != null) {
                    getVideoImage();
                    setupPlayer();
                }

            }
        });
        fragmentServiceDetialsMvvm.getSingleServiceData(service_id);


        binding.flVideo.setOnClickListener(v -> {
            isInFullScreen = true;

            binding.motionLayout.transitionToEnd();
            if (player != null) {
                player.setPlayWhenReady(true);
            }


        });



    }

    private void getVideoImage() {

        int microSecond = 6000000;// 6th second as an example
        Uri uri = Uri.parse(singleServiceDataModel.getData().getVideo());
        RequestOptions options = new RequestOptions().frame(microSecond).override(binding.imageVideo.getWidth(), binding.imageVideo.getHeight());
        Glide.with(activity).asBitmap()
                .load(uri)
                .centerCrop()
                .apply(options)
                .into(binding.imageVideo);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPlayer() {

        if (singleServiceDataModel.getData().getVideo() != null) {
            trackSelector = new DefaultTrackSelector(activity);
            dataSourceFactory = new DefaultDataSource.Factory(activity);
            MediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(singleServiceDataModel.getData().getVideo()));

            player = new ExoPlayer.Builder(activity)
                    .setTrackSelector(trackSelector)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build();

            player.setMediaItem(mediaItem);
            player.setPlayWhenReady(false);
            player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            binding.exoPlayer.setPlayer(player);
            player.prepare();

            binding.exoPlayer.setOnTouchListener((v, event) -> {
                if (player != null && player.isPlaying()) {
                    player.setPlayWhenReady(false);
                } else if (player != null && !player.isPlaying()) {

                    player.setPlayWhenReady(true);

                }
                return false;
            });


        }


    }

    public boolean isFullScreen() {
        return isInFullScreen;
    }

    public void setToNormalScreen() {

        isInFullScreen = false;
        binding.motionLayout.transitionToStart();
        if (player != null) {
            player.setPlayWhenReady(false);
        }


    }

    @Override
    public void onResume() {
        if ((Util.SDK_INT <= 23 || player == null) && singleServiceDataModel != null) {
            setupPlayer();
        }
        super.onResume();


    }

    @Override
    public void onStart() {
        if (Util.SDK_INT > 23) {
            if (player == null && singleServiceDataModel != null) {
                setupPlayer();
                binding.exoPlayer.onResume();
            }


        }
        super.onStart();


    }

    @Override
    public void onPause() {
        if (Util.SDK_INT <= 23) {
            if (player != null) {
                player.setPlayWhenReady(false);
            }
        }
        super.onPause();


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
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (Util.SDK_INT > 23) {
            if (player != null) {
                player.release();
                player = null;
            }

        }


        disposable.clear();


    }
}