package com.example.myapplication.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.controllers.lab7.Lab7Controller;
import com.example.myapplication.databinding.SigninLayoutBinding;
import com.example.myapplication.model.lab1.RegisterContents;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.example.myapplication.utils.lab6.LoadingDialog;
import com.example.myapplication.utils.lab7.Encryption;

public class SignInActivity extends ComponentActivity {

    private SigninLayoutBinding binding;
    private Lab7Controller mViewModel;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tiến hành khởi tạo layout
        binding = SigninLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Tiến hành khởi tạo controller(viewmodel)
        mViewModel = new ViewModelProvider(this).get(Lab7Controller.class);
        mViewModel.init();

        //Đăng ký branch
        RetrofitClient.Companion.setBranch("1");

        //Khởi tạo loading
        loadingDialog = new LoadingDialog(this);

        //Xử lý nút nhấn đăng nhập
        binding.btnSignIn.setOnClickListener(view -> {
            if (validate()) {

                String encrypPass= Encryption.hashPassword(binding.password.getText().toString());
                Log.i("DEBUG","Encryp pass:"+encrypPass);


                //Call api lấy dữ liệu
                mViewModel.registerDeviceApp(new RegisterContents(binding.userName.getText().toString(), "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3", "22184a05bdd68cb1", "123456", "1.5.7", "tuhDevice"));
                loadingDialog.setDialog(true);

            } else {
                Toast.makeText(this, "Không được để dữ liệu trống", Toast.LENGTH_SHORT).show();
            }
        });

        binding.forgotPassword.setOnClickListener(view -> {
            Toast.makeText(this, "Bạn đã nhấn vào quên mật khẩu", Toast.LENGTH_SHORT).show();
        });

        observeDataToken();

    }

    //Hàm này dùng để clear focus tất cả edit text khi nhấn ra ngoài màn hình
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    //Hàm này dùng để kiểm tra dữ liệu đầu vào có hợp lệ không
    private boolean validate() {
        try {
            if (binding.userName.getText().toString().isEmpty() || binding.password.getText().toString().isEmpty()) {
                return false;
            }

            return true;
        } catch (Exception er) {
            Log.e("DEBUG", "Error from validate" + er);
            return false;
        }
    }

    //Hàm này dùng để quan sát dữ liệu sau khi gọi registerDeviceApp
    private void observeDataToken() {
        mViewModel.combinedData.observe(this, data -> {
            Log.i("DEBUG", "Call observe");

            try {
                if (data.first.isEmpty() || data.second == -1) {
                    new Handler().postDelayed(() -> {
                        Toast.makeText(this, mViewModel.dataError.getValue().toString(), Toast.LENGTH_SHORT).show();
                        loadingDialog.setDialog(false);
                    }, 1000);
                } else {

                    //Set authentication token
                    RetrofitClient.Companion.setAuthToken(data.first);

                    new Handler().postDelayed(() -> {
                        loadingDialog.setDialog(false);
                        startActivity(new Intent(this, Lab6Activity.class));
                    }, 1000);

                }


            } catch (Exception er) {
                Log.e("DEBUG", "Error from observe Data Token:" + er);
            }
        });
    }

}
