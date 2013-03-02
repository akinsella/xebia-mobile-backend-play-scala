package models.web

case class Sidebar(groups:Seq[ActionGroup])
case class ActionGroup(name:String, actions:Seq[ActionItem])
case class ActionItem(message:String, url:String)
