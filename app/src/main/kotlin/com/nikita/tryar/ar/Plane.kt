package com.nikita.tryar.ar

import com.nikita.ar.from_sample.utils.MeshObject
import java.nio.Buffer

class Plane : MeshObject() {

  var verts: Buffer
  var textCoords: Buffer
  var norms: Buffer
  var indice: Buffer

  init {
    verts = fillBuffer(planeVertices)
    textCoords = fillBuffer(planeTexcoords)
    norms = fillBuffer(planeNormals)
    indice = fillBuffer(planeIndices)
  }


  override fun getBuffer(bufferType: MeshObject.BUFFER_TYPE): Buffer? {
    var result: Buffer? = null
    when (bufferType) {
      MeshObject.BUFFER_TYPE.BUFFER_TYPE_VERTEX -> result = verts
      MeshObject.BUFFER_TYPE.BUFFER_TYPE_TEXTURE_COORD -> result = textCoords
      MeshObject.BUFFER_TYPE.BUFFER_TYPE_INDICES -> result = indices
      MeshObject.BUFFER_TYPE.BUFFER_TYPE_NORMALS -> result = norms
      else -> {
      }
    }
    return result
  }


  override fun getNumObjectVertex(): Int = planeVertices.size / 3

  override fun getNumObjectIndex(): Int = planeIndices.size

  companion object {
    // Data for drawing the 3D plane as overlay
    private val planeVertices = floatArrayOf(-0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f)

    private val planeTexcoords = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)

    private val planeNormals = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f)

    private val planeIndices = shortArrayOf(0, 1, 2, 0, 2, 3)
  }
}