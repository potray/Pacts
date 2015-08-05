package es.potrayarrick.pacts.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import es.potrayarrick.pacts.backend.models.User;

public class OfyService {
    static {
        factory().register(User.class);
    }

    /**
     * A time saver. Just for typing less code.
     * @return the Objectify service.
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Another time server for typing less code.
     * @return the Objectify factory.
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
