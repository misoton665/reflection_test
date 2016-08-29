package com.example

// mocker
object Mocker {

  import scala.reflect.runtime.universe._

  implicit class MockType(typeName: String) {
    def hasPrefixOf(prefix: String): Boolean = typeName matches (prefix + "[a-z]+")

    def hasSuffixOf(suffix: String): Boolean = typeName matches (".+" + suffix)
  }

  def testSuffix(target: String, suffix: String): Boolean = {
    target hasSuffixOf suffix
  }

  def isTypeOf[T](typeSuffix: String)(obj: T)(typeTag: TypeTag[T]): Boolean = {
    val objTypeName = typeTag.tpe.toString
    objTypeName hasSuffixOf typeSuffix
  }

  def isController_[T](obj: T)(implicit typeTag: TypeTag[T]): Boolean = {
    val objTypeName = typeTag.tpe.toString
    objTypeName hasSuffixOf "Controller"
  }

  def isController[T](obj: T)(implicit typeTag: TypeTag[T]) = isTypeOf("Controller") _
}