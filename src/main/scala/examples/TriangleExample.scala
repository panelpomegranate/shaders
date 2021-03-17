package examples

import lwjglutils.LwjglApp
import org.lwjgl._
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30.{glBindVertexArray, glGenVertexArrays}

import java.nio.{FloatBuffer, IntBuffer}
import scala.io.Source

object TriangleExample extends LwjglApp {
  val vertices1: Array[Float] = Array(
    -0.9f, -0.5f, 0.0f,
    -0.0f, -0.5f, 0.0f,
    -0.45f, 0.5f, 0.0f,
  )

  val vertices2: Array[Float] = Array(
    0.0f, -0.5f, 0.0f,
    0.9f, -0.5f, 0.0f,
    0.45f, 0.5f, 0.0f
  )

  var SPOrange = 0
  var SPYellow = 0
  var vao1 = 0
  var vbo1 = 0

  var vao2 = 0
  var vbo2 = 0

  override def userInit(): Unit = {
    vao1 = glGenVertexArrays()
    vbo1 = glGenBuffers()

    vao2 = glGenVertexArrays()
    vbo2 = glGenBuffers()


    // 2. copy our vertices array in a buffer for OpenGL to use
    glBindBuffer(GL_ARRAY_BUFFER, vbo1)

    // 3. then set our vertex attributes pointers
    glBufferData(GL_ARRAY_BUFFER, vertices1, GL_STATIC_DRAW)

    //using VAO
    glBindVertexArray(vao1)
    //configure array format
    glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0)
    //enable array
    glEnableVertexAttribArray(0)

    // 2. copy our vertices array in a buffer for OpenGL to use
    glBindBuffer(GL_ARRAY_BUFFER, vbo2)

    // 3. then set our vertex attributes pointers
    glBufferData(GL_ARRAY_BUFFER, vertices2, GL_STATIC_DRAW)

    //using VAO
    glBindVertexArray(vao2)
    //configure array format
    glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3*4, 0)
    //enable array
    glEnableVertexAttribArray(0)


    val vertexShader: Int = glCreateShader(GL_VERTEX_SHADER)

    val Yellow: Int = glCreateShader(GL_FRAGMENT_SHADER)


    val vertexShaderString: String = Source.fromResource("shaders/triangleExample.vert").getLines().mkString("\n")

    {
      glShaderSource(vertexShader, vertexShaderString)
      glCompileShader(vertexShader)

      val success: Array[Int] = Array(0)

      glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success);
      if (success(0) == 0) {
        val msg = glGetShaderInfoLog(vertexShader, 512)
        println(s"Error compiling shaders: $msg")
        System.exit(0)
      } else {
        val msg = glGetShaderInfoLog(vertexShader, 512)
        println(s"Successfully compiled: $msg")
      }
    }

    val fragmentShader: Int = glCreateShader(GL_FRAGMENT_SHADER)
    val fragmentShaderString: String = Source.fromResource("shaders/triangleExample.frag").getLines().mkString("\n")

    val YellowString: String = Source.fromResource("shaders/orangeExample.frag").getLines().mkString("\n")

    {
      glShaderSource(fragmentShader, fragmentShaderString)
      glCompileShader(fragmentShader)

      glShaderSource(Yellow, YellowString)
      glCompileShader(Yellow)

      val success: Array[Int] = Array(0)

      glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, success);
      if (success(0) == 0) {
        val msg = glGetShaderInfoLog(fragmentShader, 512)
        println(s"Error compiling shaders: $msg")
        System.exit(0)
      } else {
        val msg = glGetShaderInfoLog(fragmentShader, 512)
        println(s"Successfully compiled: $msg")
      }
    }


    SPOrange = glCreateProgram()
    SPYellow = glCreateProgram()

    glAttachShader(SPOrange, vertexShader)
    glAttachShader(SPOrange, fragmentShader)
    glLinkProgram(SPOrange)

    glAttachShader(SPYellow, vertexShader)
    glAttachShader(SPYellow, Yellow)
    glLinkProgram(SPYellow)

    {
      val success: Array[Int] = Array(0)
      glGetProgramiv(SPOrange, GL_LINK_STATUS, success);
      if (success(0) == 0) {
        val res = glGetProgramInfoLog(SPOrange);
        println(s"Error linking program $res")
      }
    }


  }

  override def drawCall(): Unit = {

    // 2. use our shader program when we want to render an object
    glUseProgram(SPOrange)

    glBindVertexArray(vao1)
    //drawing vao, already bound in init  glBindVertexArray(vao)
    glDrawArrays(GL11.GL_TRIANGLES, 0, 3)

    glUseProgram(SPYellow)

    glBindVertexArray(vao2)
    //drawing vao, already bound in init  glBindVertexArray(vao)
    glDrawArrays(GL11.GL_TRIANGLES, 0, 3)



  }

}
