package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.adapter.ItemAdapter
import com.example.myapplication.adapter.Product
import com.example.myapplication.adapter.ProductType
import com.example.myapplication.databinding.Lab1LayoutBinding
import com.example.myapplication.databinding.SignInLayoutBinding
import com.example.myapplication.request.SearchContents
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    /*private lateinit var binding: SignInLayoutBinding*/
    private lateinit var binding: Lab1LayoutBinding
    private lateinit var controller: Controller
    private var productList=mutableListOf("Mango", "Strawberry", "Avocado")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=Lab1LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        var itemList= mutableListOf<Item>()

        val productTypeList1 = mutableListOf(
            ProductType("Type 1", 0),
            ProductType("Type 2", 0),
            ProductType("Type 3", 0)
        )

        val productTypeList2 = mutableListOf(
            ProductType("Type 1", 0),
            ProductType("Type 2", 0),
            ProductType("Type 3", 0)
        )

        val productTypeList3 = mutableListOf(
            ProductType("Type 1", 0),
            ProductType("Type 2", 0),
            ProductType("Type 3", 0)
        )

        itemList.add(Item(productList,productTypeList1))
        itemList.add(Item(productList,productTypeList2))
        itemList.add(Item(productList,productTypeList3))

        var adapter=ItemAdapter(itemList)
        binding.recyclerView.adapter=adapter

        binding.btnConfirm.setOnClickListener{
            var productList=adapter.getValues()
            Log.i("DEBUG","Size:${productList.size}")
            if(hasDuplicateProductName(productList)){
                Toast.makeText(this,"San pham bi trung vui long nhap lai",Toast.LENGTH_SHORT).show()
            }else if(productHasEmptyAmount(productList)){
                Toast.makeText(this,"Khong duoc de trong gia tri",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Thanh cong",Toast.LENGTH_SHORT).show()
                val customDialog = CustomDialog(this,productList)
                customDialog.show()
            }
        }

    }

    private fun hasDuplicateProductName(productList: List<Product>): Boolean {
        val seenNames = mutableSetOf<String>()
        for (product in productList) {
            if (!seenNames.add(product.productName)) {
                return true
            }
        }
        return false
    }

    private fun productHasEmptyAmount(productList: List<Product>): Boolean {
        for (product in productList) {
            for(productType in product.listProductType){
                if(productType.amount==0)
                    return true
            }
        }
        return false
    }

    /*private fun xuLyCallAPI() {
        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = ViewModelProvider(this)[Controller::class.java]
        RetrofitClient.setBranch("1")


        binding.btnSignIn.setOnClickListener {
            btnSignInClick()
        }

        binding.btnSearch.setOnClickListener {
            btnSearchClick()
        }

        observeRegister()
        observeSearch()
    }*/

    /*private fun btnSignInClick() {

        if (binding.deviceName.text!!.isNotBlank()) {
            var registerContents = RegisterContents(
                "nvtest@tmmn",
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                "22184a05bdd68cb1",
                "123456",
                "1.5.7",
                binding.deviceName.text.toString()
            )

            controller.registerDeviceApp(registerContents)
        } else {
            Toast.makeText(this, "Khong duoc de trong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun btnSearchClick() {
        if (controller.dataToken.value == null || controller.dataUserID.value == null) {
            Toast.makeText(this, "Chưa có token", Toast.LENGTH_SHORT).show()
        } else {
            var searchContents = SearchContents(-1, controller.dataUserID.value!!)
            controller.search(searchContents)
        }
    }

    private fun observeRegister() {
        controller.dataUserID.observe(this) { userID ->
            if (userID != null) {
                Toast.makeText(this, "Lấy thành công userID:${userID}", Toast.LENGTH_SHORT).show()
            }
        }

        controller.dataToken.observe(this) { token ->
            if(token!=null){
                Toast.makeText(this, "Lấy thành công token:${token}", Toast.LENGTH_SHORT).show()
                RetrofitClient.setAuthToken(token!!)
            }
        }
    }*/

    /*private fun observeSearch() {
        controller.dataStaffList.observe(this) { staffList ->
            if(staffList.isNotEmpty()){
                binding.staffList.adapter = StaffAdapter(staffList)
                Toast.makeText(this,"Lấy thành công danh sách",Toast.LENGTH_SHORT).show()
            }
        }
    }*/


}



