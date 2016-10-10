import cats.Id
import cats.free.Free
import freasymonad.cats.free

import scala.collection.mutable

@free
trait FilterStore {
  type FilterStoreF[A] = Free[FilterStoreGrammar, A]
  sealed trait FilterStoreGrammar[A]

  // abstract methods are automatically lifted into part of the grammar ADT
  def put[T](key: String, value: T): FilterStoreF[Unit]
  def get[T](key: String): FilterStoreF[Option[T]]
  def getAll[T](): FilterStoreF[List[T]]
  def remove(key: String): FilterStoreF[Unit]
  def removeAll(): FilterStoreF[Unit]

  def update[T](key: String, f: T => T): FilterStoreF[Unit] =
    for {
      vMaybe <- get[T](key)
      _      <- vMaybe.map(v => put[T](key, f(v))).getOrElse(Free.pure(()))
    } yield ()

  def getCount: FilterStoreF[Int] =
    getAll[Any].map(_.length)
}

class FilterStoreInterpreter extends FilterStore.Interp[Id] {
  val kvs = mutable.Map.empty[String, Any]

  override def get[T](key: String): Id[Option[T]] =
    kvs.get(key).map(_.asInstanceOf[T])

  override def put[T](key: String, value: T): Id[Unit] =
    kvs(key) = value

  override def getAll[T]: Id[List[T]] =
    kvs.values.map(_.asInstanceOf[T]).toList

  override def remove(key: String): Id[Unit] = kvs -= key

  override def removeAll(): Id[Unit] = kvs.clear()
}
