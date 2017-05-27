package com.nikita.tryar.navigation.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.nikita.tryar.R
import com.nikita.tryar.navigation.base.*
import java.io.FileWriter
import java.util.*

class NavActivity : Activity() {

    private val ROOM_WIDTH = 10.86
    private val ROOM_LENGTH = 8.6

    private val REQUEST_ENABLE_BT = 1
    private val DETAIL = 2


    lateinit var bluetoothAdapter: BluetoothAdapter

    lateinit var room: Room
    lateinit var trilateration: Trilateration
    lateinit var myMainHolder: MainHolder
    lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        myMainHolder = MainHolder.getInstance()
        myMainHolder.context = applicationContext

        path = Environment.getExternalStorageDirectory().toString() + "/Android/data/ble.com.kaliningrad.ble/log.txt"

        setupRoom()
        setupBluetooth()
    }

    override fun onResume() {
        super.onResume()

        if (!bluetoothAdapter.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish()
            Toast.makeText(this, "Сорян братух, включи блютуз", Toast.LENGTH_LONG).show()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupRoom() {
        room = Room.getInstance()
        room.width = ROOM_WIDTH
        room.length = ROOM_LENGTH

        val radius: Double = 3.0

        val ex1 = Exhibit(10.86, 0.0)
        ex1.name = "Дыры"
        ex1.description = "Знакомство с прекрасным не может обойтись без современного искусства. Обратите внимание на потолок. Нами представлена инсталляция под интригующим названием «Вентиляционные отверстия: шаг в будущее». Художники, искусно прятавшие свое истинное лицо под платьем простых отделочников, своим произведением поставили нас с вами в тупик. Вентиляционные отверстия. Путь к свету? Образ бесконечности? Что хотели донести до нас мастера? Какие символы и смыслы прячутся за этими грубыми решетками? Искусствоведы музея по сей день ведут ожесточенные споры по поводу истинного смысла данного художественного объекта."
        ex1.radius = radius
        room.addExhibit(ex1)

        val ex2 = Exhibit(0.0, 2.4)
        ex2.name = "Лампа Аладдина"
        ex2.description = "Перед Вами - волшебная лампа Аладдина. Хоть она и похожа на простой настенный светильник, это не так. У нее множество чудесных свойств. Если например потереть ее левой рукой, можно выйграть Хакафон. Попробуйте и убедитесь сами.\n\nПеред Вами - волшебная лампа Аладдина. Хоть она и похожа на простой настенный светильник, это не так. У нее множество чудесных свойств. Если например потереть ее левой рукой, можно выйграть Хакафон. Попробуйте и убедитесь сами.\n\nПеред Вами - волшебная лампа Аладдина. Хоть она и похожа на простой настенный светильник, это не так. У нее множество чудесных свойств. Если например потереть ее левой рукой, можно выйграть Хакафон. Попробуйте и убедитесь сами."
        ex2.radius = radius
        room.addExhibit(ex2)

        val ex3 = Exhibit(0.0, 8.0)
        ex3.description = "Инсталляция «Сейф неизвестности». Само название говорит нам о некоей тайне, нераскрытой загадке. Авторы данного художественного объекта  не оставили пояснений  по поводу смысла и назначения их детища. Но, как истинные ценители искусства, обратимся к нашему художественному чутью. Несомненно, за серой, якобы случайно и небрежно закапанной краской оболочкой, скрывается многое... быть может, толстое стекло отделяет нас от машины времени, замаскированной под ненужный компьютерный хлам? Современное искусство не дает нам прямых ответов. Скорее порождает в нас сплошные вопросы..."
        ex3.name = "Сейф"
        ex3.radius = radius
        room.addExhibit(ex3)

        val ex4 = Exhibit(8.86, 7.6)
        ex4.description = "Сейчас нас ждет встреча с уникальным элементом интерьера.  Ширма — своеобразная перегородка, служащая местом для переодевания, а может скрывающая множество тайн и интриг! (фото девушки за ширмой) \n" + "Благодаря простоте и незатейливости исполнения ,как видите, сами посетители галереи решили использовать данный художественный экспонат по прямому назначению. Сотрудники галереи не стали препятствовать этому. Так, натуралистичность и художественная правда всегда находят живой и непосредственный отклик в сердцах истинных ценителей прекрасного!"
        ex4.name = "Ширма"
        ex4.radius = radius
        room.addExhibit(ex4)

        trilateration = Trilateration(room.width, room.length)
    }

    private fun setupBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.startLeScan { device, rssi, scanRecord ->
            runOnUiThread {
                Log.d("qwe", device.toString())
                if (device.name != null && device.name.contains("tag")) {
                    var number = 0
                    try {
                        number = Integer.parseInt(device.name.substring(3, device.name.length))
                    } catch (e: Exception) {
                        Toast.makeText(this,
                                "Зарегистрировано непонятное устройство", Toast.LENGTH_LONG).show()
                        number = 0
                    }

                    var myBluetoothDevice = room.getByNumber(number)
                    if (myBluetoothDevice == null) {
                        myBluetoothDevice = MyBluetoothDevice(device.name)
                        room.add(myBluetoothDevice)
                    }
                    myBluetoothDevice.addRssi(rssi)
                    if (room.deviceCount == 3) {// !!! == 3
                        val p = trilateration.getTrilateratedPiont(room.getByNumber(1).meters,
                                room.getByNumber(2).meters,
                                room.getByNumber(3).meters)
                        refreshRssis(p)
                        val a1 = room.currentExpCount/* * room.getDeviceCount()*/
                        val a2 = myMainHolder.expCount * room.deviceCount
                        if (a1 == a2) {
                            checkNearestExhibit(p)
                        }
                    } else {
                        refreshRssis(null)
                    }
                    Collections.sort(room.myBluetoothDevices)
                }
            }
        }
    }

    fun checkNearestExhibit(p: Point) {
        if (myMainHolder.isInSpeak)
            return
        if (myMainHolder.isInLaunch)
            return
        val size = room.exhibits.size
        for (i in 0..size - 1) {
            val exhibit = room.exhibits[i]
            val pe = exhibit.point
            val radius = exhibit.radius
            if (Math.abs(p.x - pe.x) < radius
                    && Math.abs(p.y - pe.y) < radius
                    && !exhibit.isReached) {
                myMainHolder.isInLaunch = true
                exhibit.isReached = true
                switchToExcibitWithNumber(exhibit.id)
                return
            }
        }
    }

    fun switchToExcibitWithNumber(number: Int) {
        Toast.makeText(this, "switchToExcibitWithNumber: " + number, Toast.LENGTH_LONG).show()
    }

    fun refreshRssis(p: Point?) {
        if (p != null)
            (findViewById(R.id.field3) as TextView).text = room.desc + "\nx: " + String.format("%.1f", p.x) + " y: " + String.format("%.1f", p.y) + " " + myMainHolder.isInLaunch + " " + myMainHolder.isInSpeak
        else
            (findViewById(R.id.field3) as TextView).text = room.desc
        saveLogFile(room.log)
        room.needToRefresh()
    }

    fun saveLogFile(s: String) {
        val f: FileWriter
        try {
            f = FileWriter(path, true)
            f.write(s)
            f.flush()
            f.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v("QWE", e.localizedMessage)
        }
    }

}