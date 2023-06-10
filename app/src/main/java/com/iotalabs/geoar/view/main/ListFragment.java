package com.iotalabs.geoar.view.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
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

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private DataBaseViewModel dataBaseViewModel;
    private ListView friendListView;
    private MyAdapter myAdapter;
    public static ArrayList<FriendData> fData = new ArrayList<>();
    private DbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private FriendData friendData;
    private GetFriendData getTask;
    private static String IP_ADDRESS;
    private DeleteFriendData task;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private ClassUUID classUUID;
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


        mDbOpenHelper = new DbOpenHelper(getActivity());

        friendListView = binding.swipeMenuListFriend;
        myAdapter=new MyAdapter(getContext(),fData);
        friendListView.setAdapter(myAdapter);

        doWhileCursorToArray();

        // 데이터 입력받을 Adapter 생성


        // 땡길수 있는 리스트 생성

        swipeMenuListView = binding.swipeMenuListFriend;
        swipeMenuListView.setAdapter(myAdapter);
        swipeMenuListView.setMenuCreator(creator);
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

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                task = new DeleteFriendData(getActivity());
                task.execute("http://" + IP_ADDRESS + "/deleteFriend.php", StaticUUID.UUID,fData.get(position).UUID,String.valueOf(fData.get(position)._id));
                getTask= new GetFriendData(getContext());//친구 위치정보 받기
                getTask.execute( "http://" + IP_ADDRESS + "/getMyFriend.php", StaticUUID.UUID);
                //Toast.makeText(getActivity().getApplication(), "정보삭제!", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        //////////

        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        doWhileCursorToArray();
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
    @SuppressLint("Range")
    private void doWhileCursorToArray(){
        fData.clear();
        mDbOpenHelper.open();
        mCursor = null;
        mCursor = mDbOpenHelper.getAllColumns();
        while (mCursor.moveToNext()) {
            friendData = new FriendData(
                    mCursor.getInt(mCursor.getColumnIndex("_id")),
                    mCursor.getString(mCursor.getColumnIndex("UUID")),
                    mCursor.getString(mCursor.getColumnIndex("name"))
            );
            fData.add(friendData);
        }
        mCursor.close();
        if(fData.isEmpty()){
            binding.textViewNoFriend.setVisibility(View.VISIBLE);
        }
        else {
            binding.textViewNoFriend.setVisibility(View.INVISIBLE);
        }
        mDbOpenHelper.close();
        myAdapter.notifyDataSetChanged();
    }

    public class DeleteFriendData  extends AsyncTask<String, Void, String> {
        private Activity activity;
        private ProgressDialog progressDialog;
        private DbOpenHelper mDbOpenHelper;

        public  DeleteFriendData(Activity activity){
            this.activity=activity;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(activity, "Please Wait", null, true, true);
            progressDialog.setCanceledOnTouchOutside(false);//바깥터치X
            progressDialog.setCancelable(false);//뒤로가기X
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String result;
            String TO_FRIEND = (String)params[1];
            String FROM_FRIEND = (String)params[2];

            String serverURL = (String)params[0];
            int position = Integer.parseInt(params[3]);
            String postParameters = "TO_FRIEND=" + TO_FRIEND + "&FROM_FRIEND=" + FROM_FRIEND ;
//두번째부턴 &를 붙여야함

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();
                result = sb.toString();
                if(result.equals("삭제 완료!")){//서버의 데이터 베이스에서 삭제 되었을 경우 내부 데이터베이스도 삭제
                    mDbOpenHelper = new DbOpenHelper(activity);
                    mDbOpenHelper.open();
                    mDbOpenHelper.deleteColumn(position);
                    mDbOpenHelper.close();
                    toast("삭제 완료!");
                }
                else{
                    toast("친구삭제에 실패했습니다.");
                }

                backDoWhileCursorToArray();
                return sb.toString();

            } catch (Exception e) {
                toast("친구삭제에 실패했습니다.");
                return new String("Error: " + e.getMessage());
            }

        }
    }
    public void backDoWhileCursorToArray(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                doWhileCursorToArray();
            }
        });
    }
    public void toast(String msg){
        handler2.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),msg , Toast.LENGTH_SHORT).show();
            }
        });
    }
}