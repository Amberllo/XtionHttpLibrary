package net.xtion.crm.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import net.xtion.crm.cache.MemoryCache;
import net.xtion.crm.service.LoginService;
import net.xtion.crm.R;
import net.xtion.crm.entity.LoginByMobileEntity;

import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    void login() {
        MemoryCache.getInstance().put("usernumber","644300");
        Action1<LoginByMobileEntity> success = new Action1<LoginByMobileEntity>() {
            @Override
            public void call(LoginByMobileEntity loginByMobile) {
                Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            }
        };

        Action1<Throwable> error = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Toast.makeText(MainActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };

        LoginService.loginByMoble("644300","888888",true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success,error);
    }



}
