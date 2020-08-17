package com.example.firstkotlinapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.viewpager2.widget.ViewPager2
import com.example.firstkotlinapp.dataClass.BoardVO
import com.example.firstkotlinapp.fragment.FirstFragment
import com.example.firstkotlinapp.fragment.SecondFragment
import com.example.firstkotlinapp.fragment.ThirdFragment
import com.example.firstkotlinapp.recycler.fragment.FragmentAdapter
import com.example.firstkotlinapp.retrofit.OkHttpManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.schedulers.Schedulers
import me.relex.circleindicator.CircleIndicator3


class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private val TAG = "MainActivity"

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* ViewPager 2 예제*/
        /*val texts = listOf(
            "text1",
            "text2",
            "text3",
            "text4",
            "text5",
            "text6",
            "text7"
        )

        val viewPager  = findViewById<ViewPager2>(R.id.viewPager)
        val viewPageAdapter = ViewPageAdapter(texts)
        viewPager.adapter = viewPageAdapter*/


        /*프래그먼트 생성 및 리스트에 넣기*/
        var firstFragment = FirstFragment.newInstance("test")
        Log.d(TAG, firstFragment.toString())
        val secondFragment = SecondFragment()
        val thirdFragment = ThirdFragment()

        var fragmentList = arrayListOf(firstFragment, secondFragment, thirdFragment)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        var fragmentAdapter =
            FragmentAdapter(
                supportFragmentManager,
                lifecycle,
                fragmentList
            )
        viewPager.adapter = fragmentAdapter


        //현재 위치 기준으로 viewPager좌우에  각 몇개의 화면을 미리 생성할지 설정
        viewPager.offscreenPageLimit = 1

        /*TabLayout*/
        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val tabListitem = listOf("tab1", "tab2", "tab3")

        /*TabLayoutMediator(tablayout,viewPager){tab,position->
            tab.text = tabListitem[position]
        }.attach()*/

        /*TabLayoutMediator(tablayout, viewPager, object : TabLayoutMediator.TabConfigurationStrategy{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position){
                    0 -> tab.customView = createTabView("Tab1")
                    1 -> tab.customView = createTabView("Tab2")
                    2 -> tab.customView = createTabView("Tab3")
                }
            }
        }).attach()*/

        TabLayoutMediator(tablayout, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.customView = createTabView("Cust_Tab1")
                    1 -> tab.customView = createTabView("Cust_Tab2")
                    2 -> tab.customView = createTabView("Cust_Tab3")
                }
            }).attach()


        /*indicator*/
        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(viewPager)

        indicator.createIndicators(fragmentList.size, 0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    viewPager.currentItem = position
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicator.animatePageSelected(position)
            }
        })

//        val testService:BoardService = Retrofit.Builder()
//            .baseUrl("http://175.196.190.80:8080")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(BoardService::class.java)
//
//        val repos: Call<List<BoardVO>> = testService.getData()


         /*OkHttpManager.KotlinOKHttpRetrofitRxJavaManager.getInstance().getData()
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe({
                Log.d(TAG, "성공")
                Log.d(TAG, it.toString())
                val str = StringBuffer()
                for (board: BoardVO in it) {
                    str.append(board)
                    str.append("\n")
                }
                (viewPager.adapter as FragmentAdapter).notifyItemChanged(0, str)
                Log.d(TAG, "notify했음")

            }, {
                Log.d(TAG, "실패")
                Log.d(TAG, it.toString())
            })*/


        /*val call: Call<List<BoardVO>> = retrofit.getData()
        call.enqueue(object : Callback<List<BoardVO>> {
            override fun onFailure(call: Call<List<BoardVO>>, t: Throwable) {
                Log.d(TAG, "실패")
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<BoardVO>>, response: Response<List<BoardVO>>) {
                Log.d(TAG, "성공")
                Log.d(TAG,  response.body().toString())
            }
        })*/
    }

    private fun createTabView(tabName: String): View? {
        val tabView: View = LayoutInflater.from(this).inflate(R.layout.tab_list_custom, null)
        val textView = tabView.findViewById<TextView>(R.id.txt_name)
        textView.text = tabName
        return tabView
    }

}
