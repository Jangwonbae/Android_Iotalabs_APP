package com.iotalabs.geoar.view.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.FragmentListBinding;
import com.google.android.gms.maps.model.LatLng;
import com.iotalabs.geoar.data.ClassUUID;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.util.db.DbOpenHelper;
import com.iotalabs.geoar.view.main.adapter.friend_list.FriendData;
import com.iotalabs.geoar.util.network.GetFriendData;
import com.iotalabs.geoar.view.main.adapter.friend_list.MyAdapter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private DataBaseViewModel dataBaseViewModel;
    private MyAdapter myAdapter;
    public ArrayList<FriendData> friends;
    private SwipeMenuListView swipeMenuListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Fragment 바인딩
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container,false);
        //뷰모델 생성
        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        //뷰모델 연결
        binding.setViewModel(dataBaseViewModel);
        binding.textViewNoFriend.setVisibility(View.INVISIBLE);

        //친구 목록을 담을 리스트
        friends = dataBaseViewModel.getMyFriendList().getValue();

        //swipeMenuListView 생성
        swipeMenuListView = binding.swipeMenuListFriend;
        swipeMenuListView.setMenuCreator(creator);

        myAdapter=new MyAdapter(getContext(),friends);
        swipeMenuListView.setAdapter(myAdapter);

        if(friends.isEmpty()){
            binding.textViewNoFriend.setVisibility(View.VISIBLE);
        }
        else {
            binding.textViewNoFriend.setVisibility(View.INVISIBLE);
        }

        //swipeMenuListView 리스트 열었다 닫았다 메소드
        swipeMenuListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
                swipeMenuListView.smoothOpenMenu(position);
            }
            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                swipeMenuListView.smoothOpenMenu(position);
            }
        });
        //열려있을때 메뉴 클릭 메소드
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //삭제할때 메소드
                dataBaseViewModel.removeFriend(friends.get(position).UUID);
                return true;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //구독하는 친구들 리스트가 바뀌면 실행
        dataBaseViewModel.myFriendList.observeInOnStart(this, new Observer<ArrayList<FriendData>>() {//점유권을 가져와야됨
            @Override
            public void onChanged(ArrayList<FriendData> friendDatalist) {
                createList(friendDatalist);
            }
        });
        dataBaseViewModel.getAllUserData();
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density; return Math.round((float) dp * density);
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override// list 땡가는 메뉴 만들기
        public void create(SwipeMenu menu) {
            // create "첫번째" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getActivity());
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(dpToPx(90));
            // set a icon
            deleteItem.setIcon(R.drawable.ic_baseline_delete_forever_24);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
    public void createList(ArrayList<FriendData> friendDatalist){
        friends.clear();//비우고 다시 채우기
        for(FriendData fData: friendDatalist){
            friends.add(fData);
        }//why? notifyDataSetChanged() 얘는 friends= friendDatalist 이런식으로 하면 갱신이 안되더라

        if(friends.isEmpty()){
            binding.textViewNoFriend.setVisibility(View.VISIBLE);
        }
        else {
            binding.textViewNoFriend.setVisibility(View.INVISIBLE);
        }
        myAdapter.notifyDataSetChanged();
    }
}