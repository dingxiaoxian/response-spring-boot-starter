#!/usr/bin/env bash
GPG_KEY_ID='执行`gpg --list-secret-keys` 中Hex格式字符的末尾8位' \
GPG_PRIVATE_KEY_PASSWORD="gpg key 生成时填写的密码" \
GPG_PRIVATE_KEY="执行`gpg --export-secret-keys --armor 你的邮箱 |grep -v '\-\-' |grep -v '=.' |tr -d '\n'`所产生的结果" \
ORG_GRADLE_PROJECT_sonatypeUsername="注册https://issues.sonatype.org/时的用户名" \
ORG_GRADLE_PROJECT_sonatypePassword="注册https://issues.sonatype.org/时的密码" \
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
