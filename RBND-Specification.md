# BEHSA/RBND Role-Based Node Discovery Protocol 

* Name: BEHSA/RBND
* Version: 1
* Full Name: Behsa Role-Based Node Discovery Protocol
* Status: stable
* Editor: Isa Hekmatizadeh i.hekmatizadeh@behsacorp.com

The Role-Based Node Discovery protocol(BEHSA/RBND) governs how a group of node with different 
roles discover each other on a network.

## License
Copyright (c) 2018 Behsa Corporation.

This Specification is free software; you can redistribute it and/or modify it under the terms of 
the GNU General Public License as published by the Free Software Foundation; either version 3 of 
the License, or (at your option) any later version.

This Specification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.  

You should have received a copy of the GNU General Public License along with this program; if 
not, see <http://www.gnu.org/licenses>.

## Language
The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", 
"SHOULD", "SHOULD NOT", "RECOMMENDED",  "MAY", and "OPTIONAL" in this 
document are to be interpreted as described in RFC 2119 (see "[Key words for use in RFCs to 
Indicate Requirement Levels](http://tools.ietf.org/html/rfc2119)").

## Goals
RBND define a role-based discovery protocol, it's useful for a group of nodes with 
different roles to find and connect each other. Every node MUST have a String formatted role and 
other nodes can find that node address(ip address with port) and role without any pre-configuration.
Nodes MAY join(connect) and leave(disconnect) arbitrary, also they MAY change their address.

## Architecture
RBND uses UDP broadcast or multicast. Implementations of this specification SHOULD implement at 
least one of them or MAY implement both of them and choose one of them based on the configuration.

RBND does not use any heartbeat strategy to track other nodes, it's up to client(user) to 
implement it and notify the RBND library which node does not response anymore.

RBND nodes use simple JOIN and LEAVE message to notify other nodes about the state of itself. 

## Message Header
first 6 byte of any RBND message is its header, 4 of them representing "RBND" the protocol name, 
one byte with value 0x1 represent protocol version which is in this specification 1. sixth byte 
defines the message type and could be one of the values 0x1 for JOIN and 0x2 for JOIN Response 
and 0x3 for LEAVE.

## JOIN
When a node connect to the network it MUST send a broadcast or multicast JOIN message to other
.JOIN message header is 'R'-'B'-'N'-'D'-0x1-0x1, and its follows by 2 byte representing node 
port and the rest of the message is the Role string representation of the node who sends the 
message.

when a node receive a JOIN message, it should store IP address of the sender as well as port and 
role, then it SHOULD broadcast or multicast a JOIN response message.

## JOIN Response
JOIN response message is same as JOIN message except the header which should be 
'R'-'B'-'N'-'D'-0x1-0x2.

when a node receive a JOIN response message it should store IP address of the sender as well as 
port and role.

## LEAVE
When a node wants to go off, it MAY send a broadcast or multicast LEAVE. LEAVE message header is 
'R'-'B'-'N'-'D'-0x1-0x3 which follows by 2 byte indicate the port node used to listen to. After 
these 8 byte header, role string comes. like other message types.

If node exceptionally disconnect from the network or crash or for any other reason it can notify 
others by LEAVE message, it's the responsibility of other nodes client to notify its RBND library
about that node not responding.

## Security
RBND does not implement any authentication, access control, or encryption mechanisms and should 
not be used in any deployment where these are required.

## Known Weaknesses
RBND does not detect the disconnection of nodes unless they announce they disconnections by the 
LEAVE message.