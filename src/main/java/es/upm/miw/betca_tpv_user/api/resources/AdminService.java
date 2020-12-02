package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.data.daos.UserSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class AdminService {

    private UserSeeder userSeeder;

    @Autowired
    public AdminService(es.upm.miw.betca_tpv_user.data.daos.UserSeeder userSeeder) {
        this.userSeeder = userSeeder;
    }

    public void deleteAllAndInitialize() {
        this.userSeeder.deleteAllAndInitialize();
    }

    public void seedDataBase() {
        this.userSeeder.deleteAllAndInitializeAndSeedDataBase();
    }
}
