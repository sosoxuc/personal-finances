package personal.utils;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DbUtils {

    public static EntityManagerFactory getEmf(Map<?, ?> properties) {
        return Persistence.createEntityManagerFactory("TURNICET", properties);
    }
}
