package in.rk.gflix.aggregator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.rk.gflix.aggregator.dto.RecommendedMovie;
import in.rk.gflix.aggregator.dto.UserGenre;
import in.rk.grpcflix.common.Genre;
import in.rk.grpcflix.movie.MovieSearchRequest;
import in.rk.grpcflix.movie.MovieSearchResponse;
import in.rk.grpcflix.movie.MovieServiceGrpc.MovieServiceBlockingStub;
import in.rk.grpcflix.user.UserGenreUpdateRequest;
import in.rk.grpcflix.user.UserResponse;
import in.rk.grpcflix.user.UserSearchRequest;
import in.rk.grpcflix.user.UserServiceGrpc.UserServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class AggregatorService {

	@GrpcClient("user-service")
	private UserServiceBlockingStub userBlockinStub;

	@GrpcClient("movie-service")
	private MovieServiceBlockingStub movieBlockinStub;

	public List<RecommendedMovie> getUserMovieSuggestions(String loginId) {
		UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder().setLoginId(loginId).build();
		UserResponse userResponse = this.userBlockinStub.getUserGenre(userSearchRequest);
		MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder().setGenre(userResponse.getGenre())
				.build();
		MovieSearchResponse movieSearchResponse = this.movieBlockinStub.getMovies(movieSearchRequest);

		return movieSearchResponse.getMoviesList().stream()
				.map(movieDto -> new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
				.collect(Collectors.toList());
	}

	public void setUserGenre(UserGenre userGenre) {
		UserGenreUpdateRequest genreUpdateRequest = UserGenreUpdateRequest.newBuilder()
				.setLoginId(userGenre.getLoginId()).setGenre(Genre.valueOf(userGenre.getGenre().toUpperCase())).build();

		UserResponse userResponse = this.userBlockinStub.updateUserGenre(genreUpdateRequest);
	}
}
