aws-scala-sbt-proguard
============

aws-scala-sbt-proguard is serverless framework template which use Scala with sbt and proguard.

Requirements
------------

* serverless framework
* sbt

Install
-----

Run install command:

```shell
sls install -u https://github.com/iwaiawi/aws-scala-sbt-proguard
```

Usage
-----

Run compile with proguard and deploy:

```shell
sbt proguard:proguard & sls deploy
```
