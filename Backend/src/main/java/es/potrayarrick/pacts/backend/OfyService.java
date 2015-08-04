package es.potrayarrick.pacts.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import es.potrayarrick.pacts.backend.models.User;

/**
 * Created by Daniel on 04-Aug-15.
 */
public class OfyService {
    static {
        factory().register(User.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
