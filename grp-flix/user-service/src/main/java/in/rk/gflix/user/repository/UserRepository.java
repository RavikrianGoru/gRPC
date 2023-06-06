package in.rk.gflix.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.rk.gflix.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
