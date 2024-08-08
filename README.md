## KT AIVLE SCHOOL 빅프로젝트 - AI 면접 시스템 `AIT`
AI 면접 시스템 구성 서버 중 BackEnd 서버로 Spring Boot로 구현.

### 기술 스택
- `JAVA` == 17
- `Spring Boot` == 3.2.7
- `build` == gradle
- `database` == MySQL

### ERD


### Building the project
Clone the project
```
$ git clone <Repository-URL>
```

Build
```
$ chmod +x ./gradlew
$ ./gradlew build
```

Run
```
$ cd build/libs/
$ java -jar -Xms2048M -Xmx2048M ait-0.0.1-SNAPSHOT.jar
```