package es.potrayarrick.pacts;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import backend.pacts.potrayarrick.es.friends.model.User;
import backend.pacts.potrayarrick.es.pacts.model.Pact;


/**
 * The Friend fragment.
 */
public class FriendFragment extends Fragment {
    /**
     * An argument name for {@link #newInstance(HashMap, ArrayList)} <code>friendInfo</code> parameter.
     */
    public static final String ARG_FRIEND_INFO = "friend";
    /**
     * An argument name for {@link #newInstance(HashMap, ArrayList)} <code>pacts</code> parameter.
     */
    public static final String ARG_PACTS = "pacts";

    /**
     * The friend.
     */
    private User friend;

    /**
     * The pacts the user has with the friend.
     */
    private ArrayList<Pact> pacts;

    /**
     * The instance of {@link es.potrayarrick.pacts.FriendFragment.OnFriendFragmentInteractionListener}.
     */
    private OnFriendFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendInfo the info of the friend (name, surname...)
     * @param pacts      the pacts the user has with {@link #friend}
     * @return A new instance of fragment FriendFragment.
     */
    @SuppressWarnings("unused")
    public static FriendFragment newInstance(final HashMap<String, String> friendInfo, final ArrayList<Pact> pacts) {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIEND_INFO, friendInfo);
        args.putSerializable(ARG_PACTS, pacts);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor.
     */
    public FriendFragment() {
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> friendInfo = (HashMap<String, String>) getArguments().getSerializable(ARG_FRIEND_INFO);
            if (friendInfo != null) {
                friend = new User();
                friend.setName(friendInfo.get(Utils.Strings.USER_NAME));
                friend.setSurname(friendInfo.get(Utils.Strings.USER_SURNAME));
                friend.setEmail(friendInfo.get(Utils.Strings.USER_EMAIL));
            }
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        // Add back button to the action bar
        setHasOptionsMenu(true);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            setHasOptionsMenu(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        return view;
    }

    @Override
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFriendFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFriendFragmentInteractionListener");
        }
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear previous menu
        menu.clear();
        // Swap menu bars.
        inflater.inflate(R.menu.menu_friend, menu);
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_create_pact:
                mListener.onCreatePactMenuPressed(friend.getEmail());
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * An interface to communicate with {@link MainActivity}.
     */
    public interface OnFriendFragmentInteractionListener {
        /**
         * Triggered when the create pact menu button is pressed to send him a friend request.
         *
         * @param receiverEmail the email of the friend.
         */
        void onCreatePactMenuPressed(String receiverEmail);
    }

}
