# Geev
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.piran-framework/geev/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.piran-framework/geev)
[![Travis IC](https://travis-ci.org/piran-framework/geev.svg?branch=master)](https://travis-ci.org/piran-framework/geev)
[![codecov](https://codecov.io/gh/piran-framework/geev/branch/master/graph/badge.svg)](https://codecov.io/gh/piran-framework/geev)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f436671f55984fb79910aeff17a571d6)](https://www.codacy.com/app/esahekmat/geev?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=piran-framework/geev&amp;utm_campaign=Badge_Grade)
[![Javadocs](http://javadoc.io/badge/com.piran-framework/geev.svg)](http://javadoc.io/doc/com.piran-framework/geev)

Geev is an implementation of the Piran Role-Based Node Discovery (PIRAN/RBND) version 1 
specification. Geev is a simple library you can add to your application and use it to discover 
other nodes and their roles. To understand the protocol see [RBND-specification](RBND-Specification.md)

## The Name
Geev is a mythical hero of the Iranian historical and mythical epic book, Shahnaame. Who went abroad
and searched several years to find Kay-khosrow the king of the Iran. He finally found the king and
brought him and his mother back to Iran.

## Usage
First add maven dependency like this:
```
<dependency>
        <groupId>com.piran-framework</groupId>
        <artifactId>geev</artifactId>
        <version>0.3-RELEASE</version>
</dependency>
```
Then you can create a new geev object like this:
```
Geev geev = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> /* do what you want when a new node found*/)
            .onLeave((node) -> /* also do what you want when a node left*/)
            .setMySelf(new Node("YourNodeRole",yourInetAddress,yourPort))
            .build()
```
It start a background thread to do discovery.

## Spring-boot starter
A Spring-boot-starter has been particularly designed for Geev which could be mounted on 
Spring-boot applications. By adding @EnableGeevContainer class-level annotation on the 
Configuration class, Geev starts and scans all the beans with @GeevHook annotation. Note 
that, if a class is marked with @GeevHook, that class would be qualified to be a Spring 
component bean as well, so there would be no need to add @Component or @Service by doing so.
Every GeevHook class can contain methods annotated by @NodeJoined or @NodeLeft. These method 
invoked when a new node joined or a existing node left.
To use geev starter add this dependency to your project:
```
<dependency>
        <groupId>com.piran-framework</groupId>
        <artifactId>geev-spring-boot-starter</artifactId>
        <version>0.3-RELEASE</version>
</dependency>
```
###Spring-Boot properties
To add geev-starter to a spring-boot application, add three properties besides spring-boot 
properties:
geev.broadcast #to indicate use broadcast strategy. default true
geev.multicast-address #indicate the multicast address used in multicast strategy
geev.discovery-port  #which port geev use default 5172
geev.myself-role #role of the node
geev.myself-ip #ip of the node
geev.myself-port #port of the node

They are the same as GeevConfig fields.

## Build
You need jdk >= 1.8 and maven to build geev. simply use maven to build and install the artifact 
into your local repository by the command:
```
mvn install
```
Then you can add geev into your project POM file like this:
```
<dependency>
        <groupId>com.piran-framework</groupId>
        <artifactId>geev</artifactId>
        <version>0.3-RELEASE</version>
</dependency>
```

## Contribution
Any contributions are welcomed. Also if you find any problem using geev you can create issue in 
github issue tracker of the project. There is just one limitation for the contribution and it's 
respect the code style located in code-style.xml

## License
Copyright (c) 2018 Isa Hekmatizadeh.

Geev is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser 
General Public License as published by the Free Software Foundation, either version 3 of the 
License, or (at your option) any later version.

Geev is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU Lesser General 
Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
