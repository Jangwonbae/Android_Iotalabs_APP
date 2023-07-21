package com.iotalabs.geoar.view.main.activity.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.FragmentListBinding;
import com.iotalabs.geoar.view.main.activity.DataBaseViewModel;
import com.iotalabs.geoar.view.main.adapter.MyAdapter;
import com.iotalabs.geoar.view.main.data.FriendData;
import com.iotalabs.geoar.view.main.util.swipe_menu_list.SwipeMenuListCreator;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private DataBaseViewModel dataBaseViewModel;
    private MyAdapter myAdapter;
    public ArrayList<FriendData> friends = new ArrayList<>();
    private SwipeMenuListView swipeMenuListView;
    private SwipeMenuListCreator swipeMenuListCreator;

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

        swipeMenuListCreator=new SwipeMenuListCreator(getResources());
        //swipeMenuListView 생성
        swipeMenuListView = binding.swipeMenuListFriend;
        swipeMenuListView.setMenuCreator(swipeMenuListCreator.getCreator(getContext()));

        myAdapter=new MyAdapter(getContext(),friends);
        swipeMenuListView.setAdapter(myAdapter);

        if(friends.isEmpty()){
            binding.textViewNoFriend.setVisibility(View.VISIBLE);
        }
        else {
            binding.textViewNoFriend.setVisibility(View.INVISIBLE);
        }

        initListClick();

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
    }

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
    public void initListClick(){

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
                dataBaseViewModel.removeFriend(friends.get(position).getUUID());
                return true;
            }
        });
    }
}