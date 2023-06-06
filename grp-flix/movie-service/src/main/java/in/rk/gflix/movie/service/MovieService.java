package in.rk.gflix.movie.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import in.rk.gflix.movie.repository.MovieRepository;
import in.rk.grpcflix.movie.MovieDto;
import in.rk.grpcflix.movie.MovieSearchRequest;
import in.rk.grpcflix.movie.MovieSearchResponse;
import in.rk.grpcflix.movie.MovieServiceGrpc.MovieServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MovieService extends MovieServiceImplBase{

	@Autowired
	private MovieRepository repo;
	
	@Override
	public void getMovies(MovieSearchRequest request, StreamObserver<MovieSearchResponse> responseObserver) {

		List<MovieDto> movieDtoList = this.repo.getMovieByGenreOrderByYearDesc(request.getGenre().toString())
				 .stream()
				 .map(movie->MovieDto.newBuilder()
					 					.setTitle(movie.getTitle())
					 					.setYear(movie.getYear())
					 					.setRating(movie.getYear())
					 					.build())
				 .collect(Collectors.toList());
		
		responseObserver.onNext(MovieSearchResponse.newBuilder().addAllMovies(movieDtoList).build());
		responseObserver.onCompleted();
	}

}
