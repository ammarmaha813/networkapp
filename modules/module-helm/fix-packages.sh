#!/bin/bash

# نبدلو الـ packages فكل الملفات
find src -name "*.java" -exec sed -i 's/package com.eya.securityplatform.security/package net.thevpc.samples.springnuts.keycloak/g' {} \;
find src -name "*.java" -exec sed -i 's/import com.eya.securityplatform.security/import net.thevpc.samples.springnuts.keycloak/g' {} \;

# نبدلو كذلك فالـ resources
find src -name "*.properties" -exec sed -i 's/com.eya.securityplatform.security/net.thevpc.samples.springnuts.keycloak/g' {} \;
