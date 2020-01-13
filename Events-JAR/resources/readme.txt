#----------------------------Permissions to use----------------------------#
events.staff - all commands, reload excluded
events.moderator - all commands used for moderating events (kicking, baning
events.<command> - allows using of defined command (some of them are connected, like events.join allows usage of /e and /e leave)

#---------------------------------Commands---------------------------------#  
(player)
  - /e - joins active event, or teleports at event/checkpoint
  - /e quit - leaves active event

(staff)
  - /e create <eventName> - creates event teleport for
  - /e remove <eventName> | /e remove (if standing in event location) -
  - /e start <eventName> | /e a - Activates and event with given name
  - /e end - Ends an event, deletes everyones inventory and sends everyone on spawn
  - /e modify <eventName> - list of possible modifications
  - /e checkpoint - checkpoint manager
  - /e tp | /e tp <event> - teleports to event without being registered as being at event
  - /e kick <player> - kicks player from active event
  - /e ban <player> - bans player from active event
  - /e b <message> - announces an event with specified message