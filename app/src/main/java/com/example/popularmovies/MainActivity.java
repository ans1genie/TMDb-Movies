package com.example.popularmovies;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.databinding.ActivityMainBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;
import com.example.popularmovies.repository.Repository;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;
import com.example.popularmovies.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private String ApiKey= BuildConfig.ApiKey;
    ArrayList<Movie> movieList=new ArrayList<>();
    private RecyclerView recyclerView;
    private ActivityMainBinding activityMainBinding;
    private MainActivityViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel= ViewModelProviders.of(MainActivity.this).get(MainActivityViewModel.class);
        activityMainBinding= DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        getSupportActionBar().setTitle("Popular Movies");
        getData();
        swipeRefreshLayout=activityMainBinding.swiperefresh;
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });


    }

    private void getData() {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        Call<MovieDBResponse> call=movieDataService.getPopularMovies(ApiKey);
        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse=response.body();
                if(movieDBResponse!=null&&movieDBResponse.getMovies()!=null)
                {
                    movieList=(ArrayList<Movie>)movieDBResponse.getMovies();
                    for(Movie m:movieList)
                    {
                        viewModel.AddMovie(m);
                    }
                    showOnRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Error!"+ t.getMessage().trim(),Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },5000);
            }
        });

    }

    private void showOnRecyclerView() {
        recyclerView=activityMainBinding.rv;
        MoviesAdapter moviesAdapter= new MoviesAdapter(MainActivity.this,movieList);
        if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        }
        else
        {recyclerView.setLayoutManager((new GridLayoutManager(MainActivity.this,4)));}
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}