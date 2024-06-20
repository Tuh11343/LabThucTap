package com.example.myapplication.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.adapter.lab3.Lab3ItemAdapter
import com.example.myapplication.adapter.lab3.Lab3Listener
import com.example.myapplication.controllers.lab3.Lab3Controller
import com.example.myapplication.controllers.lab3.Lab3ControllerFactory
import com.example.myapplication.databinding.Lab6LayoutBinding
import com.example.myapplication.model.lab1.RegisterContents
import com.example.myapplication.model.lab1.SearchContents
import com.example.myapplication.model.lab1.Staff
import com.example.myapplication.model.lab2.Product
import com.example.myapplication.model.lab5.Good
import com.example.myapplication.model.lab6.Item6
import com.example.myapplication.model.lab6.ProductType6
import com.example.myapplication.network.lab1.RetrofitClient.Companion.setAuthToken
import com.example.myapplication.network.lab1.RetrofitClient.Companion.setBranch
import com.example.myapplication.utils.lab6.CustomDialogJava6
import com.example.myapplication.utils.lab6.LoadingDialog

class Lab3Activity : ComponentActivity() {
    private lateinit var binding: Lab6LayoutBinding
    private lateinit var adapter: Lab3ItemAdapter
    private lateinit var mViewModel: Lab3Controller
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Lab6LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Khởi tạo loading
        loadingDialog = LoadingDialog(this)
        loadingDialog.setDialog(true)

        //Khởi tạo viewmodel
        val factory = Lab3ControllerFactory(application)
        mViewModel = ViewModelProvider(this, factory)[Lab3Controller::class.java]

        //Đăng ký branch
        setBranch("1")
        mViewModel.registerDeviceApp(
            RegisterContents(
                "test2@tmmn",
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                "22184a05bdd68cb1",
                "123456",
                "1.5.7",
                "tuhDevice"
            )
        )

        //Kiểm tra xem dữ liệu sau khi call api có hoạt động không
        observeDataToken()
        observeData()
        testObserve()

        //Xử lý nút confirm
        btnConfirmHandle()
    }

    private fun btnConfirmHandle() {
        binding.btnConfirm.setOnClickListener {

            //Lấy danh sách sản phẩm từ dapter
            val productList = adapter.getValues()

            /*if (hasDuplicateProductName(productList)) {
                Toast.makeText(Lab4Activity.this, "San pham bi trung vui long nhap lai", Toast.LENGTH_SHORT).show();
            } else if (productHasEmptyAmount(productList)) {
                Toast.makeText(Lab4Activity.this, "Khong duoc de trong gia tri", Toast.LENGTH_SHORT).show();
            } else {
                CustomDialogJava dialog = new CustomDialogJava(Lab4Activity.this, productList);
                dialog.show();
            }*/
            val dialog = CustomDialogJava6(this, productList)
            dialog.show()
        }
    }

    //Hàm này dùng để khởi tạo adapter cho view
    private fun setUpAdapter(
        itemList: MutableList<Item6>,
        productList: List<String>,
        productTypeList: List<ProductType6>
    ) {
        adapter = Lab3ItemAdapter(itemList, object : Lab3Listener {
            //Hàm này dùng để xử lý khi thêm sản phẩm
            override fun add() {

                //Tạo mới 2 danh sách sản phẩm và loại sản phẩm tránh trường hợp sử dụng chung
                val addProductList: MutableList<String> = ArrayList()
                for (value in productList) {
                    addProductList.add(value)
                }
                val addProductTypeList: MutableList<ProductType6> = ArrayList()
                for (productType6 in productTypeList) {
                    addProductTypeList.add(
                        ProductType6(
                            productType6.name, ArrayList(
                                mutableListOf(0L, 0L)
                            )
                        )
                    )
                }

                //Gọi hàm add để thêm item vào adapter
                adapter.add(Item6(addProductList, addProductTypeList))
            }

            override fun delete(item: Item6) {
                adapter.delete(item)
            }

        })

        //Tiến hành show recyclerView
        Handler(Looper.getMainLooper()).postDelayed({
            loadingDialog.setDialog(false)
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.adapter = adapter
        }, 1000)
    }

    fun hasDuplicateProductName(productList: List<Product>): Boolean {
        val seenNames: MutableSet<String> = HashSet()
        for (product in productList) {
            if (!seenNames.add(product.productName)) {
                return true
            }
        }
        return false
    }

    private fun productHasEmptyAmount(productList: List<Product>): Boolean {
        for (product in productList) {
            for (productType in product.listProductType) {
                if (productType.amount == 0) {
                    return true
                }
            }
        }
        return false
    }

    //Hàm này dùng để quan sát giá trị của token và userID có lấy được hay chưa
    private fun observeDataToken() {
        mViewModel.combinedData.observe(
            this
        ) { data: Pair<String, Int> ->
            if (mViewModel.dataToken.value != null) {

                //Set token cho client
                setAuthToken(data.first!!)

                //Đây là chỗ xử lý bắt đầu call api lấy danh sách dữ liệu cần thiết
                /*--------------------------------------------------------------------------------*/
                //Call API lấy danh sách nhân viên
                val searchContents = SearchContents(-1, 755)
                mViewModel.search(searchContents)

                //Call API lấy danh sách sản phẩm
                mViewModel.getGoods()
            } else {
                Log.e("DEBUG", "Data Token is null")
            }
        }
    }

    //Hàm này dùng để quan sát dữ liệu 2 danh sách nhân viên và sản phẩm
    private fun observeData() {
        mViewModel.combinedListData.observe(
            this
        ) { data: Pair<List<Staff>, List<Good>> ->
            try {
                if (data.first != null && data.first!!.isEmpty()) {
                    Toast.makeText(this, "Danh sách nhân viên rỗng", Toast.LENGTH_SHORT).show()
                    return@observe
                }
                if (data.second != null && data.second!!.isEmpty()) {
                    Toast.makeText(this, "Danh sách sản phẩm rỗng", Toast.LENGTH_SHORT).show()
                    return@observe
                }

                //Khởi tạo list item cho adapter
                val itemList: MutableList<Item6> = ArrayList()

                //Lấy danh sách sản phẩm
                val goodList = data.second

                //Khởi tạo danh sách loại sản phẩm bao gồm tên và import,export(Giá trị ban đầu là 0,0)
                val productTypeAdapterList: MutableList<ProductType6> =
                    ArrayList()
                for (good in goodList!!) {
                    productTypeAdapterList.add(
                        ProductType6(good.name, ArrayList(mutableListOf(0L, 0L)))
                    )
                }

                //Khởi tạo danh sách sản phẩm
                val productList: MutableList<String> = ArrayList()
                for (staff in data.first) {
                    productList.add(staff.name)
                }

                //Khởi tạo giá trị ban đầu cho adapter
                itemList.add(Item6(productList, productTypeAdapterList))

                //Khởi tạo adapter
                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                )
                setUpAdapter(itemList, productList, productTypeAdapterList)
            } catch (er: Exception) {
                Log.e("DEBUG", "Error from observeData:$er")
            }
        }
    }

    //Hàm này dùng để check xem controller có hoạt động không
    private fun testObserve() {
        mViewModel.dataStaffList.observe(
            this
        ) { staffList: List<Staff?> ->
            Log.i("DEBUG", "StaffList:" + staffList.size)
        }
        mViewModel.dataGoodList.observe(
            this
        ) { goods: List<Good?> ->
            Log.i("DEBUG", "Goods:" + goods.size)
        }
        mViewModel.dataToken.observe(
            this
        ) { token: String ->
            Log.i("DEBUG", "DataToken:$token")
        }
    }
}
