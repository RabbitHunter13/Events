#----------------------------Permissions to use----------------------------#
events.staff
events.moderator - all commands used for moderating events (kicking, baning
events.<command> - allows using of defined command (some of them are connected, like events.join allows usage of /e and /e leave)

#---------------------------------Commands---------------------------------#  
(player)
  - /e - joins active event
  - /e quit | leave - leaves active event

(staff)
  - /e create <eventName> - creates event teleport for
  - /e remove <eventName> | /e remove (if standing in event location) -
  - /e activate <eventName> | /e a - Activates and event with given name
  - /e end - Ends an event, deletes everyones inventory and sends everyone on spawn
  - /e modify <eventName> - list of possible modifications
  - /e tp | /e tp <event> - teleports to event without being registered as being at event
  - /e kick <player> - kicks player from active event
  - /e ban <player> - bans player from active event
  - /e b <time> | /e broadcast <time> - announces an event with specified start time