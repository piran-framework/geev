# Geev
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.behsacorp/geev/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.behsacorp/geev)
[![Travis IC](https://travis-ci.org/behsa-oss/geev.svg?branch=master)](https://travis-ci.org/behsa-oss/geev)
[![codecov](https://codecov.io/gh/behsa-oss/geev/branch/master/graph/badge.svg)](https://codecov.io/gh/behsa-oss/geev)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f436671f55984fb79910aeff17a571d6)](https://www.codacy.com/app/esahekmat/geev?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=behsa-oss/geev&amp;utm_campaign=Badge_Grade)
[![Javadocs](http://javadoc.io/badge/com.behsacorp/geev.svg)](http://javadoc.io/doc/com.behsacorp/geev)

Geev is an implementation of the Behsa Role-Based Node Discovery (BEHSA/RBND) version 1 
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
        <groupId>com.behsa</groupId>
        <artifactId>geev</artifactId>
        <version>0.1-RELEASE</version>
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

## Build
You need jdk >= 1.8 and maven to build geev. simply use maven to build and install the artifact 
into your local repository by the command:
```
mvn install
```
Then you can add geev into your project POM file like this:
```
<dependency>
        <groupId>com.behsa</groupId>
        <artifactId>geev</artifactId>
        <version>0.2-SNAPSHOT</version>
</dependency>
```

## Contribution
Any contributions are welcomed. Also if you find any problem using geev you can create issue in 
github issue tracker of the project.

## License
Copyright (c) 2018 Behsa Corporation.

Geev is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser 
General Public License as published by the Free Software Foundation, either version 3 of the 
License, or (at your option) any later version.

Geev is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU Lesser General 
Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
