# JAADec
**This is a fork of https://sourceforge.net/projects/jaadec/ containing fixes to make it play nice with other Java Sound Providers.**

The original project was licensed under Public Domain and as such this fork is also licensed under Public Domain. Use as you like!

JAAD is an AAC decoder and MP4 demultiplexer library written completely in Java. It uses no native libraries, is platform-independent and portable. It can read MP4 container from almost every input-stream (files, network sockets etc.) and decode AAC-LC (Low Complexity) and HE-AAC (High Efficiency/AAC+).

This library is available on Bintray's `jcenter` as a Maven/Gradle download.<br>
https://bintray.com/dv8fromtheworld/maven/JAADec/view

## Showbie Maven Central

https://github.com/showbie/maven-central-repository/packages/187074

Publishing to Showbie's github maven central please ensure you created your personal access token.  Follow these instructions located here:  https://github.com/showbie/maven-central-repository

To publish enter the following command:

`GITHUB_REGISTRY_USERNAME=<username> GITHUB_REGISTRY_TOKEN=<your personal access token>  mvn deploy`
