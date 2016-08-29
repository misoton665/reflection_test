package com.example

object Hello {

  def main(args: Array[String]): Unit = {
    println("Hello, world!")
    val co = GetUserController
    println(Mocker.isController(co))

    println(Mocker.testSuffix("Mogehoge", "hoge"))
  }
}

case class Po(o: Int)

// adaptor
trait Controller[I, O] {
  def doCall(arg: I): O
}

trait IGetUserController extends Controller[UserId, UserDTO] with UsesGetUserUseCase {
  override def doCall(arg: UserId): UserDTO = {
    getUserUseCase.execute(arg)
  }
}

object GetUserController extends IGetUserController with MixInGetUserUseCase

trait UserRepositoryImpl extends UserRepository {
  private var users: List[User] = List()

  override def store(entity: User): Unit = {
    users = users :+ entity
  }

  override def find(id: User#ID): Option[User] = {
    users.find(_.id == id)
  }

  override def delete(id: User#ID): Unit = {
    users = users.filter(_.id != id)
  }
}

trait MixInUserRepository extends UsesUserRepository {
  val userRepository = new UserRepository with UserRepositoryImpl
}

trait MixInGetUserUseCase extends UsesGetUserUseCase {
  val getUserUseCase = new GetUserUseCase with MixInUserRepository
}
// useCase
trait UseCase[I, O] {
  def execute(arg: I): O
}

case class UserDTO(name: String)

trait GetUserUseCase extends UseCase[UserId, UserDTO] with UsesUserRepository {
  override def execute(arg: UserId): UserDTO = {
//    val user = userRepository.find(arg)
//    UserDTO(user.name.value)
  }
}

trait UsesGetUserUseCase {
  val getUserUseCase: GetUserUseCase
}

// entity
trait Identifier[+T] {
  val value: T
}

trait IntId extends Identifier[Int]

trait Entity[T <: Identifier[_]] {
  type ID = T

  val id: T
}

trait Repository[E <: Entity[_]] {
  def store(entity: E): Unit
  def find(id: E#ID): E
  def delete(id: E#ID): Unit
}

case class UserId(value: Int) extends IntId

case class UserName(value: String)

case class User(id: UserId, name: UserName) extends Entity[UserId]

trait UserRepository extends Repository[User]

trait UsesUserRepository {
  val userRepository: UserRepository
}