package com.example.administrator.networktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/***
 * 用PULL和SAX方法解析XML数据和发送网络请求，获取网络返回的数据
 * XML数据：
 * <apps>
 *     <app>
 *         <id>1</id>
 *         <name>Goodle Maps</name>
 *         <version>1.0</version>
 *     </app>
 *      <app>
 *         <id>2</id>
 *         <name>Chrome</name>
 *         <version>2.1</version>
 *     </app>
 *      <app>
 *         <id>3</id>
 *         <name>Google Play</name>
 *         <version>2.3</version>
 *     </app>
 * </apps>
 * JSON数据：
 * [{"id":"5","name":"clash of clans","version":"5.5"},
 * {"id":"6","name":"Boom Beach","version":"7.0"},
 * {"id":"7","name":"Clash Royale","version":"3.5"}]
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sendButton;
    private TextView responseText;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.send);
        responseText = findViewById(R.id.response_text);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       requestWithOKHttp();

    }

    /**
     * 通过第三方库OKHttp来发送网络请求，获取网络的服务器返回的数据
     */
    private void requestWithOKHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建OkHttpClient的实例
                    OkHttpClient client = new OkHttpClient();
                    //发送HTTP请求，创建Request对象通过.url来设置访问网络的地址
                    Request request = new Request.Builder()
                            .url("https://www.baidu.com")
                            .build();
                    //调用OkHttpClient的newCall()方法来创建一个Call对象，并调用它的execute（）方法来发送请求并获取服务器返回的数据
                    Response response = client.newCall(request).execute();
                    //把服务器获取到的数据转换成字符串类型
                    String responseData = response.body().toString();
                    parseXMLWithPull(responseData);
                    showResponse(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * pull解析XML数据
     *
     * @param responseData
     */
    private void parseXMLWithPull(String responseData) {
        try {
            //创建XmlPullParserFactory 实例，借助它得到XmlPullParser对象
            XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser pullParser = pullParserFactory.newPullParser();
            //调用setInput()
            pullParser.setInput(new StringReader(responseData));
            int eventType = pullParser.getEventType();
            String id = "";
            String name = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = pullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("id".equals(nodeName)) {
                            id = pullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name = pullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:
                        if ("app".equals(nodeName)) {
                            Log.d(TAG, "parseXMLWithPull: id is" + id);
                            Log.d(TAG, "parseXMLWithPull: name is" + name);
                        }
                        break;
                    default:
                        break;
                }
                eventType = pullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * SAX解析XML数据
     * 1、创建一个类ContentHandler，继承DefaultHandler
     * 2、创建SAXParserFactory的对象，在获取到XMLReader对象
     * 3、把编写的ContentHandler的实例设置到XMLReader中
     * 4、最后调用parser（）方法进行解析
     */
    public void parseXMLWithSAX(String xmlData){

        try {
            SAXParserFactory factory=SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler=new ContentHandler();
            //将ContentHandler的实例设置到XMLReader中
            xmlReader.setContentHandler(handler);
            //开始执行解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 使用JSONObject解析json数据
     */
    public void parseJSONWithJSONObject(String jsonData){

        try {
            JSONArray  jsonArray = new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String id=jsonObject.getString("id");
                String name=jsonObject.getString("name");
                String version=jsonObject.getString("version");
                Log.d(TAG, "endElement: id is"+id);
                Log.d(TAG, "endElement: name is"+name );
                Log.d(TAG, "endElement: version is"+version);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     * 用GSON解析json数据，可以将一段JSON格式的字符串自动映射成一个对象
     * 这样即使是解析JSON数组也很简单，只需要把JSON数据自动解析成一个对象就好了
     * Gson gson=new Gson();
     * Person person=gson.fromJson(jsonData,Person.class);
     * 如果解析的是JSON数组就需要借助TypeToken解析的数据类型传入fromJson（）方法中
     * List<Person> people=gson.fromJson(jsonData,new TypeToken<List<Person>>(){}.getType());
     * 1、在项目中添加对GSON库的依赖:
     *  implementation'com.google.code.gson:gson:2.7'
     *  2、创建需要一个App类，把解析的数据都放在里面
     *  3、开始解析数据
     */
    public void parseJSONWithGSON(String jsonData){
        Gson gson=new Gson();
        List<App> appList=gson.fromJson(jsonData,new TypeToken<List<App>>(){}.getType());
        for (App app:appList) {
            Log.d(TAG, "endElement: id is"+app.getId());
            Log.d(TAG, "endElement: name is"+app.getName() );
            Log.d(TAG, "endElement: version is"+app.getVersion());
        }
    }
    /**
     * 在runOnUiThread()线程中将子线程切换到主线程来更新UI元素，因为Android是不允许在子线程进行UI操作的
     *
     * @param data
     */
    private void showResponse(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(data);
            }
        });
    }


}
