package com.nikita.tryar.ar

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.nikita.ar.from_sample.Plane
import com.nikita.ar.from_sample.SampleAppRenderer
import com.nikita.ar.from_sample.SampleAppRendererControl
import com.nikita.ar.from_sample.SampleApplicationSession
import com.nikita.ar.from_sample.utils.CubeShaders
import com.nikita.ar.from_sample.utils.SampleUtils
import com.nikita.ar.from_sample.utils.Texture
import com.vuforia.*
import java.nio.charset.Charset
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MegaRenderer(private val activity: Activity,
                   private val session: SampleApplicationSession) : GLSurfaceView.Renderer, SampleAppRendererControl {
  var textures: Vector<Texture> = Vector()

  // SampleAppRenderer used to encapsulate the use of RenderingPrimitives setting
  // the device mode AR/VR and stereo mode
  private var mSampleAppRenderer = SampleAppRenderer(this, activity, Device.MODE.MODE_AR, false, .01f, 100f)

  private var shaderProgramID: Int = 0
  private var vertexHandle: Int = 0
  private var textureCoordHandle: Int = 0
  private var mvpMatrixHandle: Int = 0
  private var texSampler2DHandle: Int = 0
  private var calphaHandle: Int = 0

  private var mRenderer: Renderer? = null

  private var t0: Double = -1.0

  private val mPlaneObj = Plane()

  private var mIsActive = false

  // ratio to apply so that the augmentation surrounds the vumark
  private val VUMARK_SCALE = 1.02f
  private var currentVumarkIdOnCard: String? = null

  // Called when the surface is created or recreated.
  override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
    Log.d("", "GLRenderer.onSurfaceCreated")

    // Call Vuforia function to (re)initialize rendering after first use
    // or after OpenGL ES context was lost (e.g. after onPause/onResume):
    session.onSurfaceCreated()

    mSampleAppRenderer.onSurfaceCreated()
  }

  // Called to draw the current frame.
  override fun onDrawFrame(gl: GL10) {
    if (!mIsActive)
      return

    // Call our function to render content from SampleAppRenderer class
    mSampleAppRenderer.render()
  }

  // Called when the surface changed size.
  override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
    Log.d("", "GLRenderer.onSurfaceChanged")

    // Call Vuforia function to handle render surface size changes:
    session.onSurfaceChanged(width, height)

    // RenderingPrimitives to be updated when some rendering change is done
    mSampleAppRenderer.onConfigurationChanged(mIsActive)

    // Initializes rendering
    initRendering()
  }

  // Function for initializing the renderer.
  private fun initRendering() {
    mRenderer = Renderer.getInstance()

    GLES20.glClearColor(0.0f, 0.0f, 0.0f, if (Vuforia.requiresAlpha()) 0.0f else 1.0f)

    for (t in textures) {
      GLES20.glGenTextures(1, t.mTextureID, 0)
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0])
      GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
      GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
      GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
        t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
        GLES20.GL_UNSIGNED_BYTE, t.mData)
    }

    shaderProgramID = SampleUtils.createProgramFromShaderSrc(
      CubeShaders.CUBE_MESH_VERTEX_SHADER,
      CubeShaders.CUBE_MESH_FRAGMENT_SHADER)

    vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
      "vertexPosition")
    textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
      "vertexTexCoord")
    mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
      "modelViewProjectionMatrix")
    texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
      "texSampler2D")
    calphaHandle = GLES20.glGetUniformLocation(shaderProgramID,
      "calpha")

    // TODO Hide the Loading Dialog
  }

  fun setActive(active: Boolean) {
    mIsActive = active

    if (mIsActive)
      mSampleAppRenderer.configureVideoBackground()
  }

  // The render function called from SampleAppRendering by using RenderingPrimitives views.
  // The state is owned by SampleAppRenderer which is controlling it's lifecycle.
  // State should not be cached outside this method.
  override fun renderFrame(state: State, projectionMatrix: FloatArray) {
    // Renders video background replacing Renderer.DrawVideoBackground()
    mSampleAppRenderer.renderVideoBackground()

    GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    GLES20.glEnable(GLES20.GL_CULL_FACE)
    GLES20.glCullFace(GLES20.GL_BACK)

    GLES20.glEnable(GLES20.GL_BLEND)
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

    var gotVuMark = false

    var markerValue = ""
    var indexVuMarkToDisplay = -1

    if (state.numTrackableResults > 1) {
      var minimumDistance = java.lang.Float.MAX_VALUE
      val cameraCalibration = CameraDevice.getInstance().cameraCalibration
      val screenSize = cameraCalibration.size
      val screenCenter = Vec2F(screenSize.data[0] / 2.0f, screenSize.data[1] / 2.0f)

      for (tIdx in 0..state.numTrackableResults - 1) {
        val result = state.getTrackableResult(tIdx)
        if (result.isOfType(VuMarkTargetResult.getClassType())) {
          val point = Vec3F(0f, 0f, 0f)
          val projection = Tool.projectPoint(cameraCalibration, result.pose, point)

          val distance = distanceSquared(projection, screenCenter)
          if (distance < minimumDistance) {
            minimumDistance = distance
            indexVuMarkToDisplay = tIdx
          }
        }
      }

    }

    // Did we find any trackables this frame?
    for (tIdx in 0..state.numTrackableResults - 1) {
      val result = state.getTrackableResult(tIdx)
      val trackable = result.trackable
      val modelViewMatrix_Vuforia = Tool
        .convertPose2GLMatrix(result.pose)
      val modelViewMatrix = modelViewMatrix_Vuforia.data

      if (result.isOfType(VuMarkTargetResult.getClassType())) {
        val vmtResult = result as VuMarkTargetResult
        val vmTgt = vmtResult.trackable as VuMarkTarget

        val vmTmp = vmTgt.template

        val instanceId = vmTgt.instanceId
        val isMainVuMark = indexVuMarkToDisplay < 0 || indexVuMarkToDisplay == tIdx
        gotVuMark = true

        if (isMainVuMark) {
          markerValue = instanceIdToValue(instanceId)
          val instanceImage = vmTgt.instanceImage

          if (!markerValue.equals(currentVumarkIdOnCard, ignoreCase = true)) {
            blinkVumark(true)
          }
        }
        val textureIndex = 0

        // deal with the modelview and projection matrices
        val modelViewProjection = FloatArray(16)

        // Add a translation to recenter the augmentation
        // on the VuMark center, w.r.t. the origin
        val origin = vmTmp.origin
        val translX = -origin.data[0]
        val translY = -origin.data[1]
        Matrix.translateM(modelViewMatrix, 0, translX, translY, 0f)

        // Scales the plane relative to the target
        val vumarkWidth = vmTgt.size.data[0]
        val vumarkHeight = vmTgt.size.data[1]
        Matrix.scaleM(modelViewMatrix, 0, vumarkWidth * VUMARK_SCALE,
          vumarkHeight * VUMARK_SCALE, 1.0f)

        Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0)

        // activate the shader program and bind the vertex/normal/tex coords
        GLES20.glUseProgram(shaderProgramID)

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
          false, 0, mPlaneObj.getVertices())
        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
          GLES20.GL_FLOAT, false, 0, mPlaneObj.getTexCoords())

        GLES20.glEnableVertexAttribArray(vertexHandle)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)

        // activate texture 0, bind it, and pass to shader
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
          textures.get(textureIndex).mTextureID[0])
        GLES20.glUniform1i(texSampler2DHandle, 0)
        GLES20.glUniform1f(calphaHandle, if (isMainVuMark) blinkVumark(false) else 1.0f)

        // pass the model view matrix to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
          modelViewProjection, 0)

        // finally draw the plane
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
          mPlaneObj.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
          mPlaneObj.getIndices())

        // disable the enabled arrays
        GLES20.glDisableVertexAttribArray(vertexHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
        SampleUtils.checkGLError("Render Frame")
      }

    }

    if (gotVuMark) {
      // If we have a detection, let's make sure
      // the card is visible
      currentVumarkIdOnCard = markerValue
      // TODO show mark info
    } else {
      // We reset the state of the animation so that
      // it triggers next time a vumark is detected
      blinkVumark(true)
      // We also reset the value of the current value of the vumark on card
      // so that we hide and show the mumark if we redetect the same vumark instance
      currentVumarkIdOnCard = null
    }
    GLES20.glDisable(GLES20.GL_DEPTH_TEST)
    GLES20.glDisable(GLES20.GL_BLEND)

    mRenderer?.end()
  }

  private fun instanceIdToValue(instanceId: InstanceId): String {
    val instanceIdBuffer = instanceId.buffer
    val instanceIdBytes = ByteArray(instanceIdBuffer.remaining())
    instanceIdBuffer.get(instanceIdBytes)

    return when (instanceId.dataType) {
      InstanceId.ID_DATA_TYPE.STRING -> String(instanceIdBytes, Charset.forName("US-ASCII"))
      else -> return "Unknown"
    }
  }

  private fun blinkVumark(reset: Boolean): Float {
    if (reset || t0 < 0.0f) {
      t0 = System.currentTimeMillis().toDouble()
    }
    if (reset) {
      return 0.0f
    }
    val time = System.currentTimeMillis().toDouble()
    val delta = time - t0

    if (delta > 1000.0f) {
      return 1.0f
    }

    if (delta < 300.0f || delta > 500.0f && delta < 800.0f) {
      return 1.0f
    }

    return 0.0f
  }

  private fun distanceSquared(p1: Vec2F, p2: Vec2F): Float {
    return (Math.pow((p1.data[0] - p2.data[0]).toDouble(), 2.0) + Math.pow((p1.data[1] - p2.data[1]).toDouble(), 2.0)).toFloat()
  }
}