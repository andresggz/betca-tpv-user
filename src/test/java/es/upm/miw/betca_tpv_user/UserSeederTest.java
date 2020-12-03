package es.upm.miw.betca_tpv_user;

import es.upm.miw.betca_tpv_user.data.daos.UserSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserSeederTest {

    @Autowired
    public UserSeederTest(UserSeeder userSeeder) {
        userSeeder.deleteAllAndInitializeAndSeedDataBase();
    }

}
