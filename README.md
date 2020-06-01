# Maven Conventional Changelog generator for Java

Allows for automatic [conventional](https://www.conventionalcommits.org/en/v1.0.0/) changelog generation for 
Java / Maven projects based on `GIT VCS` history. 

## Maven Plugin

### Info

Plugin requires you to follow [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/) specifications in 
your commits in order to create a valid changelog entries, if one or more of the commits are not compliant they will be ignored.


#### Install the Plugin
 ```xml
    <plugins>
        <plugin>
            <groupId>com.github.eugenenosenko</groupId>
            <artifactId>changelog-maven-plugin</artifactId>
            <version>x.x.x</version>
        </plugin>
    </plugins>
```

#### Plugin configuration

Plugin allows you to specify following options:
- `<release>` specifies number of releases to generate changelog. Default value is `-1` which will generate entries for entire git history 
`<release>0</release>` will generate changelog only for last release, `1` and `2` for last 2 and 3 respectively.

- `filename` specifies filename changelog should be written into
- `amendLastCommit` specifies whether last commit should be amended to include changelog file changes, this will perform: 
    * soft reset
    * add changelog file changes 
    * commit with the same message as before
    * in case any tags where associated with the previous commit it will reapply the tag

See full configuration below

```xml
        <plugin>
            <groupId>com.github.eugenenosenko</groupId>
            <artifactId>changelog-maven-plugin</artifactId>
            <version>x.x.x</version>
            <configuration>
                <amendLastCommit>true</amendLastCommit>         <!-- default is "false"-->
                <filename>mychangelog.md</filename>             <!-- default is "CHANGELOG.md"-->
                <release>2</release>                            <!-- default is "-1"-->
                <backupFilename>backupfile.md</backupFilename>  <!-- default is "CHANGELOG.md.backup"-->
            </configuration>
        </plugin>
```

#### Usage

```shell script
mvn conventional:changelog 
```

You can specify configuration parameters from command line same as for any other plugin, i.e. 

```shell script
mvn conventional:changelog -Drelease=1 -Dfilename=changelog.md -DamendLastCommit=true
```