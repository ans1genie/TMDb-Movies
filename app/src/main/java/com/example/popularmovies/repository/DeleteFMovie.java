package com.example.popularmovies.repository;

import android.os.AsyncTask;

import com.example.popularmovies.db.FavouriteMoviesDAO;
import com.example.popularmovies.model.Movie;

public class DeleteFMovie extends AsyncTask<Movie, Void,Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public DeleteFMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.deleteFMovie(movies[0]);
        return null;
    }
}
