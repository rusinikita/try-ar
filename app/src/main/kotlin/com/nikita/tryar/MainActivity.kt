package com.nikita.tryar

import android.app.Activity
import android.os.Bundle
import android.os.RemoteException
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.WindowManager
import android.view.View
import android.widget.Toast
import com.nikita.tryar.ar.ARDelegate
import com.nikita.tryar.ar.GLView
import com.nikita.tryar.item_info.ItemInfoDelegate
import io.reactivex.disposables.CompositeDisposable
import org.altbeacon.beacon.*

const val REDMI_3_BEACON = "2dc60a0a-c9b9-41f2-9bf9-608daae095f0"
const val BEACON_160302 = "0x34316463613633666339"
const val BEACON_160225 = "0x61343035356463643833"

class MainActivity : Activity(), BeaconConsumer {

  private lateinit var beaconManager: BeaconManager

  private lateinit var glView: GLView
  private lateinit var arDelegate: ARDelegate

  private lateinit var bottomSheet: NestedScrollView
  private lateinit var iteminfoDelegate: ItemInfoDelegate

  private lateinit var testButton: FloatingActionButton

  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    beaconManager = BeaconManager.getInstanceForApplication(this)
    // AltBeacon
    //beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
    // Eddystone
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"))
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"))
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"))
    beaconManager.bind(this)

    bottomSheet = findViewById(R.id.bottom_sheet) as NestedScrollView
    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    iteminfoDelegate = ItemInfoDelegate(bottomSheet, bottomSheetBehavior, this)
    iteminfoDelegate.init()

    testButton = findViewById(R.id.testButton) as FloatingActionButton
    testButton.setOnClickListener {
      Events.recognitions.onNext("cheer")
    }

    glView = findViewById(R.id.gl_view) as GLView
    arDelegate = ARDelegate(this, glView)
    arDelegate.onCreate()
  }

  override fun onPause() {
    super.onPause()
    arDelegate.onPause()
  }

  override fun onResume() {
    super.onResume()
    arDelegate.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    arDelegate.onDestroy()
    beaconManager.unbind(this)
  }

  override fun onBeaconServiceConnect() {
    beaconManager.addRangeNotifier { beacons, region ->
      beacons.forEach {
          Log.e("RANGING","Distance - ${it.distance}, Id1 - ${it.id1}")
      }
    }
    try {
      beaconManager.startRangingBeaconsInRegion(Region("rangingid", null, null, null))
    } catch (e: RemoteException) {
      Log.e("QQQ-QQQ", e.localizedMessage)
    }
  }
}