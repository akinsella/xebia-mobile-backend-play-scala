package models.web

import securesocial.core.Identity

case class Header(user:Option[Identity] = None, menu:Option[Menu] = None, sidebar:Option[Sidebar] = None)
