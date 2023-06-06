package in.rk.gflix.user.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import in.rk.gflix.user.repository.UserRepository;
import in.rk.grpcflix.common.Genre;
import in.rk.grpcflix.user.UserGenreUpdateRequest;
import in.rk.grpcflix.user.UserResponse;
import in.rk.grpcflix.user.UserResponse.Builder;
import in.rk.grpcflix.user.UserSearchRequest;
import in.rk.grpcflix.user.UserServiceGrpc.UserServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserService extends UserServiceImplBase {

	@Autowired
	private UserRepository userRepo;

	@Override
	public void getUserGenre(UserSearchRequest request, StreamObserver<UserResponse> responseObserver) {

		Builder newBuilder = UserResponse.newBuilder();
		this.userRepo.findById(request.getLoginId()).ifPresent(user -> {
			newBuilder.setName(user.getName())
					  .setLoginId(user.getLogin())
					  .setGenre(Genre.valueOf(user.getGenre()));
		});
		responseObserver.onNext(newBuilder.build());
		responseObserver.onCompleted();
	}

	@Override
	@Transactional
	public void updateUserGenre(UserGenreUpdateRequest request, StreamObserver<UserResponse> responseObserver) {
		Builder newBuilder = UserResponse.newBuilder();
		
		this.userRepo.findById(request.getLoginId()).ifPresent(
				user -> {
						user.setGenre(request.getGenre().toString());
						//update the response
						newBuilder.setName(user.getName())
						  		  .setLoginId(user.getLogin())
						  		  .setGenre(Genre.valueOf(user.getGenre()));
						});
		
		responseObserver.onNext(newBuilder.build());
		responseObserver.onCompleted();
	}

}
