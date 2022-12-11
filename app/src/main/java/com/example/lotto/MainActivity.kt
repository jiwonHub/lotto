package com.example.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val clearButton: Button by lazy{ // 초기화 버튼
        findViewById<Button>(R.id.clearButton)
    }
    private val addButton: Button by lazy{ // 번호 추가하기 버튼
        findViewById<Button>(R.id.addButton)
    }
    private val runButton: Button by lazy{ // 자동 생성 시작 버튼
        findViewById<Button>(R.id.runButton)
    }
    private val numberPicker: NumberPicker by lazy{ // 번호 고르기 (NumberPicker)
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    private val numberTextViewList: List<TextView> by lazy{ // 번호 6개 TextView
        listOf<TextView>(
            findViewById<TextView>(R.id.firstNumber),
            findViewById<TextView>(R.id.secondNumber),
            findViewById<TextView>(R.id.thirdNumber),
            findViewById<TextView>(R.id.fourthNumber),
            findViewById<TextView>(R.id.fifthNumber),
            findViewById<TextView>(R.id.sixthNumber)
        )
    }

    private var didRun = false // 자동생성되었는지?

    private val pickNumberSet = hashSetOf<Int>() // 선택한 번호들

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    private fun initRunButton(){ // 자동 생성 시작 버튼 눌렀을 때
        runButton.setOnClickListener{
            val list = getRandomNumber() // getRandomNumber 함수 실행 후 list에 넣기

            didRun = true // 자동생성한 상태

            list.forEachIndexed{ index, number -> // index, number를 인자로 받아서 반복문 돌리기
                val textView = numberTextViewList[index] // textView에 numberTextViewList의 index지정

                textView.text = number.toString() // 지정된 index의 textView를 인자로 받은 number로 설정
                textView.isVisible = true // textView 보이기

                setNumberBackground(number, textView)
            }

            Log.d("MainActivity", list.toString())
        }
    }
    private fun initAddButton(){
        addButton.setOnClickListener{
            if (didRun){ // 자동 생성 되었다면?
                Toast.makeText(this, "초기화 후에 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pickNumberSet.size >= 5){ // 5개 이상 선택했다면?
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pickNumberSet.contains(numberPicker.value)){ // 이미 선택한 번호라면?
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size] // 선택한 번호들의 다음 textView. 선택한 번호가 3개면 4번째 textView.
            textView.isVisible = true
            textView.text = numberPicker.value.toString() // textView를 숫자 고르기의 숫자로 설정

            setNumberBackground(numberPicker.value, textView)

            pickNumberSet.add(numberPicker.value) // 중복 확인, 번호리스트 저장을 위해 pickNumberSet에 추가
        }
    }

    private fun setNumberBackground(number: Int, textView: TextView){
        when(number){ // 숫자별로 색깔 꾸미기
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yello)
            in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            in 41..50 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    private fun initClearButton(){ // 선택한 번호 초기화
        clearButton.setOnClickListener{
            pickNumberSet.clear() // 번호 초기화
            numberTextViewList.forEach{ // textView forEach 반복문으로 안보이게하기
                it.isVisible = false
            }
            didRun = false // 자동생성 하지 않은 상태
        }
    }

    private fun getRandomNumber(): List<Int>{ // 번호 자동 선택
        val numberList = mutableListOf<Int>().apply { // numberList에 for문으로 1~45숫자를 모두 넣음
            for (i in 1..45){
                if(pickNumberSet.contains(i)){ // 1~45사이의 숫자중에 선택한 번호가 있다면, 넣지 않는다.
                    continue
                }
                this.add(i)
            }
        }
        numberList.shuffle() // 생성된 번호를 섞기

        val newList = pickNumberSet.toList() + numberList.subList(0,6 - pickNumberSet.size)
        // 이전에 선택한 번호들 + numberList에 생성된 번호들중 앞에서부터 (6- 이전에 선택한 번호들개수)번째 까지 newList에 넣기

        return newList.sorted() // 정렬
    }
}