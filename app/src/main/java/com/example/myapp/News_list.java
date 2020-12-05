package com.example.myapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class News_list extends AppCompatActivity {
    ListView listView;
    TextView pageNo;

    public News news=new News();

    int count=1;


    SwipeRefreshLayout progressBar;


    Bundle subject;
    private int total=155;

    public class News
    {
        String[] url;
        String[] img;
        String[] title;
        String[] time;
        public News() {
        }

        public News(String[] url, String[] img, String[] title,String[] time) {
            this.url = url;
            this.img = img;
            this.title = title;
            this.time=time;
        }

        public String[] getTime() {
            return time;
        }

        public void setTime(String[] time) {
            this.time = time;
        }

        public String[] getUrl() {
            return url;
        }

        public void setUrl(String[] url) {
            this.url = url;
        }

        public String[] getImg() {
            return img;
        }

        public void setImg(String[] img) {
            this.img = img;
        }

        public String[] getTitle() {
            return title;
        }

        public void setTitle(String[] title) {
            this.title = title;
        }
    }
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        listView=(ListView)findViewById(R.id.news);
        subject=getIntent().getExtras();
        pageNo=findViewById(R.id.pageNo);
        toolbar=findViewById(R.id.news_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setSubtitle(subject.getString("key").toString());
        progressBar=findViewById(R.id.progress);

        Extraction extraction=new Extraction(this);
        extraction.execute(subject.getString("key").toString());

        progressBar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count+=1;
                if(count>total)
                {
                    Toast.makeText(News_list.this,"Sorry No More News Available",Toast.LENGTH_LONG).show();
                    count-=1;
                }
                else
                {

                    referesh();
                }
            }
        });

       /* dataExtraction=new DataExtraction(subject.getString("key"));
        Thread dataScraping =new Thread(dataExtraction);
        dataScraping.start();
        try {
            dataScraping.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }






        title=new String[dataExtraction.counter-1];
        url=new String[dataExtraction.counter-1];
        img=new String[dataExtraction.counter-1];

        url=dataExtraction.newsStore.url;
        img=dataExtraction.newsStore.img;
        title=dataExtraction.newsStore.title;
*/




        /*while(true)
        {
            count=0;
            try {
                output=new LinearLayout(News_list.this);
                output.setOrientation(LinearLayout.HORIZONTAL);

                ImageView news_img=new ImageView(News_list.this);


                URL link = new URL(img[count]);
                Bitmap bmp = BitmapFactory.decodeStream(link.openConnection().getInputStream());
                news_img.setMaxHeight(R.dimen._20sdp);
                news_img.setMaxWidth(R.dimen._30sdp);
                news_img.setImageBitmap(bmp);
                news_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(News_list.this,title[count]+"",Toast.LENGTH_LONG).show();
                    }
                });
                output.addView(news_img);

                TextView title_=new TextView(News_list.this);
                title_.setText(title[count]);
                output.addView(title_);

                listView.addView(output);

                count+=1;
           }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(News_list.this,e.getMessage(),Toast.LENGTH_LONG).show();
                break;
            }
        }*/
    }


    void referesh()
    {

        Extraction extraction=new Extraction(this);
        extraction.execute(subject.getString("key").toString());
    }
    class Extraction extends AsyncTask<String,Void,News>
    {


        private WeakReference<News_list> activityWeakReference;
        NewsStore newsStore;

        int counter = 0;
        Document doc;
        Elements details;
        String[] url=new String[20];
        String[] img=new String[20];
        String[] text=new String[20];
        String[] time=new String[20];
        Extraction(News_list activity)
        {
            activityWeakReference=new WeakReference<News_list>(activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            News_list activity=activityWeakReference.get();
            if(activity==null || activity.isFinishing()){
                return;
            }
            activity.listView.setVisibility(View.GONE);
            activity.progressBar.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(News news) {
            super.onPostExecute(news);
            News_list activity=activityWeakReference.get();
            pageNo.setText("Page "+count+" / "+total);
            if(activity==null || activity.isFinishing()){
                return;
            }
            activity.listView.setVisibility(View.VISIBLE);
            activity.progressBar.setRefreshing(false);
            try{
                MyListAdapter adapter=new MyListAdapter(News_list.this, news.getTitle(), news.getUrl(),news.getImg(),news.getTime());
                activity.listView.setAdapter(adapter);
            }
            catch (java.lang.NullPointerException e)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(News_list.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Please Turn On Wifi or Mobile Data")
                        .setCancelable(false)

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(News_list.this,"Please Turn On Wifi or Mobile Data",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("No Internet Access");
                alert.show();
            }

        }


        @Override
        protected News doInBackground(String... strings) {
            try {

                doc = Jsoup.connect("https://indianexpress.com/page/"+count+"/?s="+strings[0]).get();
                //String index=doc.getElementsByClass("search-result").get(0).children().get(0).text();
                //url=new String[Integer.parseInt(index.substring(index.indexOf("-")+1,index.indexOf("out of")))];
                //img=new String[Integer.parseInt(index.substring(index.indexOf("-")+1,index.indexOf("out of")))];
                //text=new String[Integer.parseInt(index.substring(index.indexOf("-")+1,index.indexOf("out of")))];
                //img=new String[Integer.parseInt(index.substring(index.indexOf("-")+1,index.indexOf("out of")))];
                if(doc.getElementsByClass("search-result").get(0).children().get(0).text()!="Result: 0- 0 out of 0 Article found") {
                    // get title of the page
                    // get all links
                    details = doc.getElementsByClass("details");



                    for (Element detail : details) {

                        for (Element child : detail.children()) {
                            if (child.tagName() == "p") {
                                continue;
                            }
                            else if (child.tagName() == "h3") {
                                for (Element subElement : child.children()) {
                                    //System.out.println(subElement.attr("href").toString());
                                    //System.out.println(subElement.attr("title").toString());
                                    url[counter] = subElement.attr("href").toString();
                                    text[counter] = subElement.attr("title").toString();

                                }
                            }
                            else if (child.tagName() == "div") {

                                for (Element subElement : child.children()) {
                                    for (Element subSubElement : subElement.children()) {
                                        if (subSubElement.tagName() == "img") {
                                            img[counter] = subSubElement.attr("data-lazy-src").toString();
                                        }
                                    }
                                }
                            }
                            else if (child.tagName() == "time") {

                                time[counter]=child.text();
                            }

                        }
                        counter += 1;
                    }
                    this.newsStore=new NewsStore();
                    this.newsStore.url=new String[counter-1];
                    this.newsStore.img=new String[counter-1];
                    this.newsStore.title=new String[counter-1];
                    this.newsStore.time=new String[counter-1];

                    for(int i=0;i<counter-1;i++)
                    {
                        this.newsStore.url[i]=url[i];
                        this.newsStore.img[i]=img[i];
                        this.newsStore.title[i]=text[i];
                        this.newsStore.time[i]=time[i];
                    }
                }
                news.setImg(this.newsStore.img);
                news.setTitle(this.newsStore.title);
                news.setUrl(this.newsStore.url);
                news.setTime(this.newsStore.time);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return news;
        }
    }
    @Override
    public void onBackPressed() {

        if(count<=1)
        {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            super.onBackPressed();

        }
        else
        {
            count-=1;

            referesh();
        }

    }
}
