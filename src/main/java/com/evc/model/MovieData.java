package com.evc.model;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class MovieData {
    public static Map<String, List<Movie>> getCategorizedMovies() {
        Map<String, List<Movie>> movieCategories = new LinkedHashMap<>();
        List<Movie> actionMovies = new ArrayList<>();
        actionMovies.add(new Movie("The Batman", "2022", "Action", "/com/evc/images/posters/batman.jpg", "mqqft2x_Aa4"));
        actionMovies.add(new Movie("Inception", "2010", "Action", "/com/evc/images/posters/inception.jpg", "YoHD9XEInc0"));
        actionMovies.add(new Movie("Deadpool", "2016", "Action", "/com/evc/images/posters/deadpool.jpg", "ONHBaC-pfsk"));
        actionMovies.add(new Movie("Dune", "2021", "Action", "/com/evc/images/posters/dune.jpg", "n9xhJrPXop4"));
        actionMovies.add(new Movie("Spider-Man", "2002", "Action", "/com/evc/images/posters/spiderman.jpg", "t06RUxPbp_c"));
        actionMovies.add(new Movie("John Wick", "2014", "Action", "/com/evc/images/posters/john-wick.jpg", "C0BMx-qxsP4"));
        actionMovies.add(new Movie("Mad Max", "2015", "Action", "/com/evc/images/posters/mad-max.jpg", "hEJnMQG9ev8"));
        actionMovies.add(new Movie("Extraction", "2020", "Action", "/com/evc/images/posters/extraction2020.jfif", "L6P3nI6VnlY"));
        movieCategories.put("Action", actionMovies);
        List<Movie> comedyMovies = new ArrayList<>();
        comedyMovies.add(new Movie("The Hangover", "2009", "Comedy", "/com/evc/images/posters/hangover.jpg", "tcdUhdOlz9M"));
        comedyMovies.add(new Movie("Superbad", "2007", "Comedy", "/com/evc/images/posters/superbad.jpg", "4eaZ_48ZYog"));
        comedyMovies.add(new Movie("Step Brothers", "2008", "Comedy", "/com/evc/images/posters/step-brothers.jpg", "CewglxElBK0"));
        comedyMovies.add(new Movie("Anchorman", "2004", "Comedy", "/com/evc/images/posters/anchorman.jpg", "QvJ1K0_JzFI"));
        comedyMovies.add(new Movie("Bridesmaids", "2011", "Comedy", "/com/evc/images/posters/bridesmaids.jpg", "FNppLrmdyug"));
        comedyMovies.add(new Movie("21 Jump Street", "2012", "Comedy", "/com/evc/images/posters/jump_street.jpg", "Oj55KinxZx4"));
        comedyMovies.add(new Movie("Napoleon Dynamite", "2004", "Comedy", "/com/evc/images/posters/napoleon.jpg", "W1IgJoRJ34Q"));
        comedyMovies.add(new Movie("Ferris Bueller", "1986", "Comedy", "/com/evc/images/posters/ferris.jpg", "0ZDbKhkLxTs"));
        movieCategories.put("Comedy", comedyMovies);
        List<Movie> scifiMovies = new ArrayList<>();
        scifiMovies.add(new Movie("The Matrix", "1999", "Sci-Fi", "/com/evc/images/posters/matrix.jpg", "vKQi3bBA1y8"));
        scifiMovies.add(new Movie("Blade Runner 2049", "2017", "Sci-Fi", "/com/evc/images/posters/Blade Ruuner.jpg", "gCcx85zbxz4"));
        scifiMovies.add(new Movie("Star Wars", "1977", "Sci-Fi", "/com/evc/images/posters/star_wars.jpg", "1g3_CFmnU7k"));
        scifiMovies.add(new Movie("Arrival", "2016", "Sci-Fi", "/com/evc/images/posters/arrival.jpg", "tFMo3UJ4B4g"));
        scifiMovies.add(new Movie("The Martian", "2015", "Sci-Fi", "/com/evc/images/posters/martian.jpg", "ej3ioOneTy8"));
        scifiMovies.add(new Movie("Alien", "1979", "Sci-Fi", "/com/evc/images/posters/alien.jpg", "LjLamj-b0I8"));
        scifiMovies.add(new Movie("Gravity", "2013", "Sci-Fi", "/com/evc/images/posters/gravity.jpg", "OiTiKOy59o4"));
        scifiMovies.add(new Movie("Interstellar", "2014", "Sci-Fi", "/com/evc/images/posters/interstellar.jpg", "zSWdZVtXT7E"));
        movieCategories.put("Sci-Fi", scifiMovies);
        return movieCategories;
    }
}