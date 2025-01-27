package chapters.testserver.repositories;

import org.springframework.data.jpa.repository.*;

public interface UserRepository extends JpaRepository<User, Long> {

}
