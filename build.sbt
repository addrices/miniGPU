name := "npc"
version := "1.0.0"

val sourceDir = "src/";
scalacOptions ++= Seq("-Xsource:2.11", "-unchecked", "-deprecation")
scalaSource in Compile := (baseDirectory(_/sourceDir)).value

resolvers ++= Seq(
	Resolver.sonatypeRepo("snapshots"),
	Resolver.sonatypeRepo("releases")
)


val defaultVersions = Map(
  "chisel3" -> "3.4.+",
  "chisel-iotesters" -> "1.2.+"
  )
  
libraryDependencies ++= (Seq("chisel3","chisel-iotesters").map {
  dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) })
