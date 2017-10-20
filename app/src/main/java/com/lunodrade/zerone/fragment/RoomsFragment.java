package com.lunodrade.zerone.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lunodrade.zerone.MainActivity;
import com.lunodrade.zerone.R;
import com.lunodrade.zerone.models.Room;
import com.lunodrade.zerone.models.User;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {

    @BindView(R.id.roomcardbutton)
    Button mIngressar;

    @BindView(R.id.room_block_with_card)
    LinearLayout mBlockWithRoom;

    @BindView(R.id.room_block_without_card)
    CardView mBlockWithoutRoom;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private ValueEventListener mPostListener;
    private MaterialDialog mProgressBar;

    private User mUserClass;
    private Room mRoomClass;

    private MainActivity mActivity;

    public RoomsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mProgressBar = new MaterialDialog.Builder(getActivity())
                .title("Por favor, esper")
                .content("Atualizando informações sobre sua turma")
                .progress(true, 0)
                .show();

        loadUserDatabase();
    }

    private Menu mMenuOptions;
    private MenuInflater mMenuInflater;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.empty_options, menu);

        mMenuOptions = menu;
        mMenuInflater = inflater;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rooms_exit) {

            new MaterialDialog.Builder(this.getContext())
                    .title("Deseja realmente sair desta sala?")
                    .content("Se sair, todos seus dados nesta sala serão apagados")
                    .positiveText("Sair")
                    .negativeText("Cancelar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            exitRoom();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUserDatabase() {
        // Attach a listener to read the data at our posts reference
        String uid = mFirebaseUser.getUid();

        mPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUserClass = dataSnapshot.getValue(User.class);

                if (mRoomClass == null) {
                    Log.d("RoomsFragment", "updateInterface: carregando Room");
                    loadRoom(mUserClass.activeRoomId);
                } else {
                    updateInterface(mUserClass);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.child("users/" + uid).addValueEventListener(mPostListener);
    }

    private void updateInterface(User user) {

        mUserClass = user;

        //TODO: adicionar uma checagem se o usuário não entrou por outra turma em outro device

        //      fazendo com que a mRoomClass daqui fique defaçada

        if (user.activeRoomId != null) {
            mBlockWithoutRoom.setVisibility(View.GONE);
            mBlockWithRoom.setVisibility(View.VISIBLE);

            if (mMenuOptions != null) {
                mMenuOptions.clear();
                mMenuInflater.inflate(R.menu.fragment_rooms_options, mMenuOptions);
            }

        } else {
            mBlockWithRoom.setVisibility(View.GONE);
            mBlockWithoutRoom.setVisibility(View.VISIBLE);

            if (mMenuOptions != null) {
                mMenuOptions.clear();
            }
        }

        mProgressBar.dismiss();
    }

    @OnClick(R.id.roomcardbutton)
    public void sayHi(Button button) {

        new MaterialDialog.Builder(this.getContext())
                .title("Código da turma")
                .content("Insira o código da turma da qual deseja participar")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("código", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                })
                .positiveText("Ingressar")
                .negativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        loadRoom(dialog.getInputEditText().getText().toString());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .show();
    }

    private void loadRoom(final String roomCode) {
        Log.d("RoomsFragment", "onDataChange: chamada loadroom " + roomCode);
        mDatabase.child("rooms/" + roomCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (roomCode == null) {
                    Log.d("RoomsFragment", "LoadRoom onDataChange: entrou em code==null");
                    updateInterface(mUserClass);
                } else {
                    Log.d("RoomsFragment", "LoadRoom onDataChange: entrou em possui code");
                    mRoomClass = dataSnapshot.getValue(Room.class);

                    if (mRoomClass != null) {
                        Log.d("RoomsFragment", "onDataChange: tem senha? " + mRoomClass.password);
                        Log.d("RoomsFragment", "onDataChange: RoomMembers = " + mRoomClass.members);
                        Log.d("RoomsFragment", "onDataChange: hasKey? " + mRoomClass.members.containsKey("cYYKQASlC4Nuwlp7X4OeMLHsDCt1"));

                        mUserClass.activeRoomName = mRoomClass.name;
                        mUserClass.activeRoomId = roomCode;

                        String uid = mFirebaseUser.getUid();
                        mRoomClass.members.put(uid, 279);

                        saveRoomInDatabase(mUserClass.activeRoomId);

                        updateInterface(mUserClass);
                    } else {
                        mActivity.showSnackbar(R.string.fragment_rooms_code_error);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void exitRoom() {
        Log.d("RoomsFragment", "exitRoom: Iniciando sair da Sala");

        String mBackupId = mUserClass.activeRoomId;

        mUserClass.activeRoomId = null;
        mUserClass.activeRoomName = null;

        String uid = mFirebaseUser.getUid();
        mRoomClass.members.put(uid, null);

        saveRoomInDatabase(mBackupId);
    }

    private void saveRoomInDatabase(String roomId) {
        String uid = mFirebaseUser.getUid();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/rooms/" + roomId + "/members/" + uid, mRoomClass.members.get(uid));
        childUpdates.put("/users/" + uid, mUserClass);

        mDatabase.updateChildren(childUpdates);
    }

}
