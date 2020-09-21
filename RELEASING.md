### Set a new version
```
mvn versions:set -DnewVersion=x.x.x
```

### Release a new version
```
mvn clean deploy -Preleasing
```

### Revert versions
```
mvn versions:revert
```