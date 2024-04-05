package com.blackrussia.launcher.activity;

import android.Manifest;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.blackrussia.game.R;

import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;
import java.util.List;

import com.blackrussia.game.R;
import com.blackrussia.launcher.adapter.NewsAdapter;
import com.blackrussia.launcher.model.News;

import com.blackrussia.launcher.adapter.ServersAdapter;
import com.blackrussia.launcher.model.Servers;
import com.blackrussia.launcher.other.Interface;
import com.blackrussia.launcher.other.Lists;

import com.google.firebase.database.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity{
	
	DatabaseReference databaseNews;
	NewsAdapter newsAdapter;
	
	DatabaseReference databaseServers;
	ServersAdapter serversAdapter;
	
	public static ArrayList<Servers> slist;
    public static ArrayList<News> nlist;
	
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

		Lists.slist = new ArrayList<>();
		Lists.nlist = new ArrayList<>();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://vbd.fdv.dd/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		Interface sInterface = retrofit.create(Interface.class);

		Call<List<Servers>> scall = sInterface.getServers();

		scall.enqueue(new Callback<List<Servers>>() {
			@Override
			public void onResponse(Call<List<Servers>> call, Response<List<Servers>> response) {

				List<Servers> servers = response.body();

				for (Servers server : servers) {
					Lists.slist.add(new Servers(server.getColor(), server.getDopname(), server.getname(), server.getOnline(), server.getmaxOnline()));
				}
			}

			@Override
			public void onFailure(Call<List<Servers>> call, Throwable t) {
				Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
			}
		});

		Call<List<News>> ncall = sInterface.getNews();

		ncall.enqueue(new Callback<List<News>>() {
			@Override
			public void onResponse(Call<List<News>> call, Response<List<News>> response) {

				List<News> news = response.body();

				for (News storie : news) {
					Lists.nlist.add(new News(storie.getImageUrl(), storie.getTitle(), storie.getUrl()));
				}
			}

			@Override
			public void onFailure(Call<List<News>> call, Throwable t) {
				Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
			}
		});
		
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
            } else {
				startTimer();
			}
        } else startTimer();
	}
	
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            startLauncher();
        }
    }

    private void startLauncher()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
	
	public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
	
	private void startTimer()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                startLauncher();
            }
        }, 1000L);
    }
}