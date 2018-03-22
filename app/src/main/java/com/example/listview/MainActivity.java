package com.example.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final Integer UPDATE_UI = 1;
   String title,tag,act;
   // String [] video_rec=new String [5];
    TextView mTitle,mTag,mAct;
    private List<Movie> movieList=new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == UPDATE_UI){
               // mVideo_length.setText(video_rec.length);
                mTitle.setText(title);
                mTag.setText(tag);
                mAct.setText(act);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest=(Button) findViewById(R.id.send_request);
        sendRequest.setOnClickListener(this);
       // initMovies();//初始化
        mTitle  = (TextView) findViewById(R.id.title_text);
        mTag = (TextView) findViewById(R.id.tag_text);
        mAct = (TextView) findViewById(R.id.act_text);
        MovieAdapter adapter=new MovieAdapter(MainActivity.this,R.layout.movie_item, movieList);
        ListView listView=(ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,
                                    int position,long id){
                Movie movie=movieList.get(position);
                Toast.makeText(MainActivity.this,movie.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.send_request){
            initMovies();
        }
    }

    public class MovieAdapter extends ArrayAdapter<Movie> {
        private int resourceId;
        public MovieAdapter(Context context, int textViewResourceId, List<Movie> objects){
            super(context,textViewResourceId,objects);
            resourceId=textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){//第二个参数convertView的含义：是代表系统最近回收的View
            Movie movie=getItem(position);//得到当前项的Fruit实例,即：获取每一行的数据
            // View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            /*LayoutInflater为这个子项加载传入的布局， inflate()方法三个参数：
            1.得到FruitAdapter的数据
            2.添加到父布局中
            3.表示只让我们在父布局中声明的layout属性生效*/
            View view;
            ViewHolder  viewHolder;
            if(convertView==null){//提升ListView的运行效率，如果为空，则使用LayoutInflater去加载布局
                view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
                viewHolder=new ViewHolder();//当回收为空时，创建ViewHolder对象，并将控件的实例都存在ViewHolder里
                viewHolder.movieImage=(ImageView) view.findViewById(R.id.movie_image);
                view.setTag(viewHolder);//调用View的setTag()方法，将ViewHolder对象存储在View中
            }else{
                view =convertView;//如果不为空，则直接对convertView进行重用
                viewHolder=(ViewHolder) view.getTag();//重新获取viewHolder
            }
            viewHolder.movieImage.setImageResource(movie.getImageId());
            viewHolder.movieName.setText(movie.getName());
            return view;
        }/*重写Baseadapter时，需要重写
        getCount()返回值控制该Adapter将会包含多少个列表项，就是看上去有多少行
        getItem(int position)
        getItemId(int position)它返回的是该postion对应item的id
       getView(int position,View convertView,ViewGroup parent)  该方法的返回值决定第position处的列表组件，就是在这里像是的layout和component组成的一个view*/
        class ViewHolder{//内部类ViewHolder用于对控件的实例缓存
            ImageView movieImage;
            TextView movieName;
        }
    }
    private void initMovies(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = JuheDemo.getRequest1("Transformers");
                    System.out.println(s);
                    JSONObject json = new JSONObject(s);
                    if (json.optInt("error_code") == 0){
                        //数据获取成功
                        JSONObject result = json.optJSONObject("result");
                        //json.optJSONArray("video_rec");
                        title = result.optString("title");
                        tag = result.optString("tag");
                        act = result.optString("act");
                        Message message = new Message();
                        message.arg1 = UPDATE_UI;
                        handler.sendMessage(message);
                    }else {
                        //数据获取失败
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public class Movie{
        private String name;
        private int imageId;
        public Movie(String name ,int imageId){
            this.name=name;
            this.imageId=imageId;
        }
        public String getName(){
            return name;
        }
        public int getImageId(){
            return imageId;
        }
    }
}