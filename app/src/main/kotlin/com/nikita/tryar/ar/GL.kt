package com.nikita.tryar.ar

import android.opengl.GLES30
import android.opengl.GLES30.glAttachShader
import android.opengl.GLES30.glDeleteShader
import android.util.Log

private val GL_TAG = "OpenGL"

fun checkGLError(checkFunName: String): Boolean {
  val err: Int = GLES30.glGetError()
  val hasError = err != GLES30.GL_NO_ERROR
  Log.e(GL_TAG, "GL error after $checkFunName(), gl_error_code = $err")
  return hasError
}

/**
 * @return compiled and loaded shaderId shaderId
 */
fun createShader(shaderType: Int, shaderCode: String): Int {
  val shaderId = GLES30.glCreateShader(shaderType)
  if (shaderId == 0) { // error
    checkGLError("glCreateShader")
    return 0
  }

  GLES30.glShaderSource(shaderId, shaderCode)
  GLES30.glCompileShader(shaderId)
  val compileStatus = IntArray(1)
  GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
  return if (compileStatus[0] > 0) { // all good
    shaderId
  } else {
    val logLen = IntArray(1)
    GLES30.glGetShaderiv(shaderId, GLES30.GL_INFO_LOG_LENGTH, logLen, 0)
    if (logLen[0] > 0) {
      val errorLog = GLES30.glGetShaderInfoLog(shaderId)
      Log.e(GL_TAG, "Could not compile shader:\n$shaderCode\nwith log:\n$errorLog")
    }
    glDeleteShader(shaderId)
    0
  }
}

/**
 * @return loaded gl program id
 */
fun createProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
  val vtxShader = createShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
  val fragShader = createShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

  fun clearShaders() {
    glDeleteShader(vtxShader)
    glDeleteShader(fragShader)
  }

  if (vtxShader == 0 || fragShader == 0) { // shaders not loaded
    clearShaders()
    return 0
  }

  val program = GLES30.glCreateProgram()
  if (program == 0) {
    checkGLError("glCrateProgram")
    clearShaders()
    return 0
  }

  glAttachShader(program, vtxShader)
  glAttachShader(program, fragShader)

  GLES30.glLinkProgram(program)
  val linked = IntArray(1)
  GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linked, 0)

  clearShaders()

  if (linked[0] == 0) {
    val logLen = IntArray(1)
    GLES30.glGetProgramiv(program, GLES30.GL_INFO_LOG_LENGTH, logLen, 0)
    if (logLen[0] > 0) {
      val errorLog = GLES30.glGetProgramInfoLog(program)
      Log.e(GL_TAG, "Could link program with log:\n$errorLog")
    }
    GLES30.glDeleteProgram(program)
    return 0
  }

  return program
}