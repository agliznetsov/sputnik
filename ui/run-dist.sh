#!/usr/bin/env bash
http-server dist -o -p 9000 --proxy http://localhost:8080 -c-1
