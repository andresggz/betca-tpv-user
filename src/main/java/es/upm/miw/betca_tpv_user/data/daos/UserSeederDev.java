package es.upm.miw.betca_tpv_user.data.daos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class UserSeederDev {

    @Autowired
    public UserSeederDev(UserSeeder userSeeder) {
        userSeeder.deleteAllAndInitializeAndSeedDataBase();
    }

}
