package models.web


case class Menu(items:Seq[MenuItem])
case class MenuItem(message:String, url:String)
