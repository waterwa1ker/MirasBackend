#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa.pub \
  target/tatar.by-0.0.1-SNAPSHOT.jar \
  root@91.186.197.219:~

echo 'Restart server...'

ssh -i ~/.ssh/id_rsa.pub root@91.186.197.219 << EOF

nohup java -jar tatar.by-0.0.1-SNAPSHOT.jar

EOF

echo 'Bye'